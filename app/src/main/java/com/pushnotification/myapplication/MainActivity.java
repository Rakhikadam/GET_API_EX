package com.pushnotification.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.recycler);
        receivedetails();

    }

    private void receivedetails(){

        JSONArray object = new JSONArray();
        //called volley parameter
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://lollypopy.in/iplustv_app/api/v1//tv/dashboard", null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data= null;
                        try {
                            data = response.getJSONObject("data");
                            List<Categories> category =new ArrayList<>();
                            JSONArray vlist = data.getJSONArray("video_list");
                            for (int i=0; i<vlist.length();i++){
                                JSONObject obj= vlist.getJSONObject(i);
                                String title = obj.getString("title");
                                JSONArray list = obj.optJSONArray("list");
                                Log.e("TAG", "onResponse: "+title );

                                List<Imagelist> imagelists = new ArrayList<>();
                                for (int j=0;j<list.length();j++){
                                   JSONObject listobj= list.getJSONObject(j);
                                   String listtitle=listobj.optString("title","NA");
                                   String listimage=listobj.optString("image","https://images.unsplash.com/photo-1559583985-c80d8ad9b29f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MXwxMDY1OTc2fHxlbnwwfHx8fHw%3D&w=1000&q=80");
                                   Imagelist image= new Imagelist(listtitle,listimage);
                                   imagelists.add(image);
                                }
                                Categories cat = new Categories(title,imagelists);
                                category.add(cat);

                                TitleAdpter adpter = new TitleAdpter(category);
                                recycler.setAdapter(adpter);
                                recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", "onErrorResponse: "+error.getMessage() );
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap map = new HashMap();
                map.put("x-api-key","iplustv@123");
                return map;
            }
        };
        Volley.newRequestQueue(MainActivity.this).add(jsonObjectRequest);



    }

    class TitleAdpter extends RecyclerView.Adapter<TitleAdpter.CustomViewHolder> {
        List<Categories> category;
        public TitleAdpter(List<Categories> category) {
            this.category = category;
        }

        @NonNull
        @Override
        public TitleAdpter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.title_row,parent,false);
            return new  CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TitleAdpter.CustomViewHolder holder, int position) {
            holder.title.setText(category.get(position).getTitle());
            ImageAdpter adpter1 = new ImageAdpter(category.get(position).getImagelistList());
            holder.itemRecycler.setAdapter(adpter1);
            holder.itemRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
        }

        @Override
        public int getItemCount() {
            return category.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder{
            TextView title;
            RecyclerView itemRecycler;
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                title= itemView.findViewById(R.id.title);
                itemRecycler= itemView.findViewById(R.id.item_recycler);
            }
        }
    }

    class ImageAdpter extends RecyclerView.Adapter<ImageAdpter.CustomViewHolder>{

        List<Imagelist> list;

        public ImageAdpter(List<Imagelist> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ImageAdpter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.secondrecycler,parent,false);
            CustomViewHolder holder = new CustomViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdpter.CustomViewHolder holder, int position) {
            holder.text.setText(list.get(position).getTitle());
            Glide.with(MainActivity.this).load(list.get(position).getImage()).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView text;
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                text = itemView.findViewById(R.id.text);
            }
        }

    }
static class Myclass{
    public Myclass(String myclass) {
//        this.myclass = myclass;
    }

    public static String MyConstant ="";
    Myclass myclass= new Myclass("this");
}
}