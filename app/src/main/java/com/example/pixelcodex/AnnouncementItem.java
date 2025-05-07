package com.example.pixelcodex;

import java.util.Date;

public class AnnouncementItem {
    private String description;
    private String email;
    private Date timestamp;

    public AnnouncementItem() {
        // Default constructor required for Firestore
    }

    public AnnouncementItem(String description, String email) {
        this.description = description;
        this.email = email;
        this.timestamp = new Date();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
