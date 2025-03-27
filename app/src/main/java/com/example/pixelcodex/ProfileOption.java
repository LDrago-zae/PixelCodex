package com.example.pixelcodex;

public class ProfileOption {
    private final int icon;
    private final String title;

    public ProfileOption(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
