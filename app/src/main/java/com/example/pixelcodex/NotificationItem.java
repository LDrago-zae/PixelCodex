package com.example.pixelcodex;

import com.google.firebase.Timestamp;

public class NotificationItem {
    private String description;
    private String email;
    private Timestamp timestamp;

    public NotificationItem() {
        // Default constructor required for Firestore
    }

    public NotificationItem(String description, String email, Timestamp timestamp) {
        this.description = description;
        this.email = email;
        this.timestamp = timestamp;
    }

    public String getDescription() { return description; }
    public String getEmail() { return email; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getTimestampString() {
        return timestamp != null ? timestamp.toDate().toString() : "N/A";
    }
}