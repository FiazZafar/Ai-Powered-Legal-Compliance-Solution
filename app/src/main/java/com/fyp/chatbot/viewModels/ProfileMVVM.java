package com.fyp.chatbot.viewModels;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.firebaseHelpers.UsersFB;
import com.fyp.chatbot.interfaces.UserInterFace;
import com.fyp.chatbot.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileMVVM extends ViewModel {
    UserInterFace userInterFace = new UsersFB();
    MutableLiveData<UserModel> userDetail = new MutableLiveData<>();
    MutableLiveData<Boolean> updateImage = new MutableLiveData<>();
    MutableLiveData<Boolean> myOnlineStatus = new MutableLiveData<>();


    public LiveData<Boolean> getMyOnlineStatus(){
        return myOnlineStatus;
    }
    public void setMyOnlineStatus(Boolean isOnline) {
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            Log.w("InboxMVVM", "UID is null. Cannot set online status.");
            return;
        }

        userInterFace.setOnlineStatus(uid, isOnline, onResult -> {
            if (onResult != null && onResult) {
                myOnlineStatus.setValue(onResult);
            }
        });
    }
    public LiveData<Boolean> getUpdateImageStatus(){
        return updateImage;
    }
    public void setUpdatedImageStatus(String imageUrl){
        userInterFace.setProfilePic(FirebaseAuth.getInstance().getUid(), imageUrl,onUpload -> {
            if (onUpload) updateImage.setValue(onUpload);
        });
    }
    public LiveData<UserModel> getUserDetail(){
        return userDetail;
    }
    public void setUserDetail(){
        userInterFace.fetchUserProfile(FirebaseAuth.getInstance().getUid(), user -> {
            userDetail.setValue(user);
        });
    }
}