package com.example.market.marketDatabase;

import android.graphics.Bitmap;

public class Image {
    private String path;
    private Bitmap content;

    public Image(String path, Bitmap content){
        this.path=path;
        this.content=content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getContent() {
        return content;
    }

    public void setContent(Bitmap content) {
        this.content = content;
    }
}
