package com.example.pixelcodex;

public class GameDetailsModel {
    private String title;
    private String imageUrl; // URL from Firebase Storage
    private int imageResId; // Local drawable resource ID for offline mode
    private String description;
    private String minimumRequirements;
    private String recommendedRequirements;

    // Constructor for Firebase data (online mode)
    public GameDetailsModel(String title, String imageUrl, String description, String minimumRequirements, String recommendedRequirements) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.imageResId = 0; // Not used in online mode
        this.description = description;
        this.minimumRequirements = minimumRequirements;
        this.recommendedRequirements = recommendedRequirements;
    }

    // Constructor for offline data
    public GameDetailsModel(String title, int imageResId, String description, String minimumRequirements, String recommendedRequirements) {
        this.title = title;
        this.imageUrl = null; // Not used in offline mode
        this.imageResId = imageResId;
        this.description = description;
        this.minimumRequirements = minimumRequirements;
        this.recommendedRequirements = recommendedRequirements;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }

    public String getMinimumRequirements() {
        return minimumRequirements;
    }

    public String getRecommendedRequirements() {
        return recommendedRequirements;
    }

    // Setters (useful for Firebase deserialization)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMinimumRequirements(String minimumRequirements) {
        this.minimumRequirements = minimumRequirements;
    }

    public void setRecommendedRequirements(String recommendedRequirements) {
        this.recommendedRequirements = recommendedRequirements;
    }
}