package com.example.pixelcodex;

import com.google.firebase.database.PropertyName;

public class UserItem {
    private String name;

    // Required empty constructor for Firestore
    public UserItem() {}

    public UserItem(String name) {
        this.name = name;
    }

    @PropertyName("name")
    public String getName() {
        return name != null ? name : "Unknown";
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }


}