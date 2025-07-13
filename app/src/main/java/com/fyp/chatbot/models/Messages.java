package com.fyp.chatbot.models;

public class Messages {
    public final static String AI_RESPONSE = "Gemini";
    public final static String USER_MESSAGE = "Users";

    private String message;
    private String message_type;

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    private String currentTime;
    public Messages(String message, String message_type, String currentTime) {
        this.message = message;
        this.message_type = message_type;
        this.currentTime = currentTime;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
