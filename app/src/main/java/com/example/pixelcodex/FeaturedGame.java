package com.example.pixelcodex;

public class FeaturedGame {
    private final String title;
    private final int imageResId; // Image resource ID

    public FeaturedGame(String title, int imageResId) {
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
