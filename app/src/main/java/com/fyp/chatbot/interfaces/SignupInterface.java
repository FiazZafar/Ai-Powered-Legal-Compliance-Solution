package com.fyp.chatbot.interfaces;

public interface SignupInterface {
    void signupUser(String email, String passwords,FirebaseCallback<Boolean> result);
    void loginUser(String email, String passwords,FirebaseCallback<Boolean> result);
    void setProfilePic(String userId,String imageUrl, FirebaseCallback<Boolean> result);
    void resetPassword(String email, FirebaseCallback<Boolean> result);
    void firebaseAuthWithGoogle(String idToken, FirebaseCallback<Boolean> result);

}
