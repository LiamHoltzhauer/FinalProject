package com.example.finalproject;

public class ImageModel {
    private int id;
    private String date;
    private String imageUrl;
    private String hdUrl;

    public ImageModel(int id, String date, String imageUrl, String hdUrl) {
        this.id = id;
        this.date = date;
        this.imageUrl = imageUrl;
        this.hdUrl = hdUrl;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getHdUrl() {
        return hdUrl;
    }
}