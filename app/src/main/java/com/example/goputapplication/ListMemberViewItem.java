package com.example.goputapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ListMemberViewItem {
    private String name;
    private Drawable img;
    private String idx;

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }
}
