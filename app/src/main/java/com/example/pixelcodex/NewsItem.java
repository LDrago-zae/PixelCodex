package com.example.pixelcodex;

public class NewsItem {
    private final int imageResId;
    private final String title;
    private final String subtitle;
    private final String description;
    private final String timestamp;
    private final int likeCount;
    private final int commentCount;
    private final boolean isComingSoon;

    public NewsItem(int imageResId, String title, String subtitle, String description, String timestamp, int likeCount, int commentCount, boolean isComingSoon) {
        this.imageResId = imageResId;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.timestamp = timestamp;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isComingSoon = isComingSoon;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public boolean isComingSoon() {
        return isComingSoon;
    }
}