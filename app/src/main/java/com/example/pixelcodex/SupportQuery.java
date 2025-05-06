package com.example.pixelcodex;

public class SupportQuery {
    private final String subject;
    private final String description;
    private final String email;

    public SupportQuery(String subject, String description, String email) {
        this.subject = subject;
        this.description = description;
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }
}