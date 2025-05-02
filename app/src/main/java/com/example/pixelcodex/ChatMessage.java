package com.example.pixelcodex;

public class ChatMessage {
    private final String text;
    private final boolean isFromUser;

    public ChatMessage(String text, boolean isFromUser) {
        this.text = text;
        this.isFromUser = isFromUser;
    }

    public String getText() {
        return text;
    }

    public boolean isFromUser() {
        return isFromUser;
    }
}