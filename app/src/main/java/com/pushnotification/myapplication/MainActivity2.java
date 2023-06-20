package com.pushnotification.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    ImageView camera , gallery, image;
    Button sendbutton;

    String imagedata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        image = findViewById(R.id.image);
        sendbutton = findViewById(R.id.button);
//camera on and permission on in mobile application
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 100);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,200);
            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senddetails();
            }
        });

    }

    //Volley method
    private void senddetails(){
       JSONArray array = new JSONArray();
       JSONObject object =new JSONObject();
       //POST request . put data in JSONobject

        try {
            object.put("location","THN01");
            object.put("leadId","QA4805");
            object.put("documentType","SIGN");
            object.put("docN","1684906728631.jpg");
            object.put("docSource","Verification Platform");
            object.put("fileB64",imagedata); //called method
            array.put(object);

            Log.e("TAG", "senddetails: "+array );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.POST,
                "https://apisit.piramal.com/v2/document-store/documentupload", array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("TAG", "onResponse: "+response.length() );
                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "onErrorResponse: "+error.getMessage() );

            }
        }){
            //add Headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap map = new HashMap<>();
                map.put("x-apikey","ZewzUMYRt0WumlBU4m4uWo3FHW32dY86");
                map.put("username","Santosh.Mehta");
                map.put("content-type","application/json");
                map.put("role","TECHNICAL_VERIFIER");
                return map;
            }
        };
        Volley.newRequestQueue(MainActivity2.this).add(objectRequest);
    }

    //call onActivityResult method called . capture image on the bitmap and set in imagelayout
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
           ImageView imageview = (ImageView) findViewById(R.id.image); //sets imageview as the bitmap
            imageview.setImageBitmap(image);
            imagedata= encodeImage(image);//add image in encodeImage method and String declared static becz of it is access anyway
        }
        else if (requestCode == 200){
         //   Bitmap Image = (Bitmap) data.getExtras().get("data");
           /* ImageView imageview = (ImageView) findViewById(R.id.image); //sets imageview as the bitmap
            imageview.setImageBitmap(image);
       */
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
            try {
                Bitmap image1 = getThumbnail(selectedImageUri);
                if (null != selectedImageUri) {
                    ImageView imageview = (ImageView) findViewById(R.id.image); //sets imageview as the bitmap
                    imageview.setImageURI(selectedImageUri);// update the preview image in the layout
                    imagedata= encodeImage(image1);//add image in encodeImage method and String declared static becz of it is access anyway

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }
    }

    public  Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 1.5) ? (originalSize / 1.5) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }
    private int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    // take-picture-and-convert-to-base64
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT); //
        return encImage;
    }

}