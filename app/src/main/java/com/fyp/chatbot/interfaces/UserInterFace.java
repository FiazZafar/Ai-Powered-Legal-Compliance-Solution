package com.fyp.chatbot.interfaces;



import com.fyp.chatbot.models.UserModel;

import java.util.List;

public interface UserInterFace {
    void addUser(UserModel userModel, String userId, FirebaseCallback<Boolean> result);
    void setProfilePic(String userId,String imageUrl, FirebaseCallback<Boolean> result);
    void fetchUserProfile(String userId,FirebaseCallback<UserModel> result);
}