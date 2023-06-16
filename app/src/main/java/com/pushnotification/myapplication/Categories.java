package com.pushnotification.myapplication;

import java.util.List;

public class Categories {
    String title;
    List<Imagelist> imagelistList;

    public Categories(String title, List<Imagelist> imagelistList) {
        this.title = title;
        this.imagelistList = imagelistList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Imagelist> getImagelistList() {
        return imagelistList;
    }

    public void setImagelistList(List<Imagelist> imagelistList) {
        this.imagelistList = imagelistList;
    }
}
