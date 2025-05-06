package com.example.pixelcodex;

import com.google.firebase.database.PropertyName;

public class GameItem {
    private String title;

    // Required empty constructor for Firebase
    public GameItem() {}

    public GameItem(String title) {
        this.title = title;
    }

    @PropertyName("title")
    public String getTitle() {
        return title != null ? title : "Unknown";
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }
}