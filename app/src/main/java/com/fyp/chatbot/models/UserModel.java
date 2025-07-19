package com.fyp.chatbot.models;


public class UserModel {
    String uId;
    String userName;
    String imgUrl;
    String userEmail;

    public UserModel( String uId,
                      String uName,
                      String userEmail,
                      String imgUrl) {
        this.userName = uName;
        this.uId = uId;
        this.userEmail = userEmail;
        this.imgUrl = imgUrl;
    }

    public UserModel() {
    }


    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}