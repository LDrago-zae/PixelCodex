package com.example.pixelcodex;

public class GameRequestModelClass {
    private String title;
    private String description;
    private String platform;
    private long timestamp;

    // Required empty constructor for Firebase deserialization
    public GameRequestModelClass() {
    }

    public GameRequestModelClass(String title, String description, String platform, long timestamp) {
        this.title = title;
        this.description = description;
        this.platform = platform;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}