package com.example.pixelcodex;

public class Game {
    private final String title;
    private final int imageResId;

    public Game(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}