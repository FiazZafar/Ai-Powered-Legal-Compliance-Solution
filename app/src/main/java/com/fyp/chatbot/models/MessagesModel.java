package com.fyp.chatbot.models;

public class MessagesModel {
    public final static String AI_RESPONSE = "Gemini";
    public final static String USER_MESSAGE = "Users";

    private String message;
    private String message_type;
    private String UserImage;

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    private String currentTime;
    public MessagesModel(String message, String message_type, String currentTime,String UserImage) {
        this.message = message;
        this.message_type = message_type;
        this.currentTime = currentTime;
        this.UserImage = UserImage;
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


    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }
}
