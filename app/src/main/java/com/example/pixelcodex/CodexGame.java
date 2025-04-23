package com.example.pixelcodex;

public class CodexGame {
    private final String name;
    private final String price;
    private final int imageResId;

    public CodexGame(String name, String description, String price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
}
