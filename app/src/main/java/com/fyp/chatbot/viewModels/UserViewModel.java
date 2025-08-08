package com.fyp.chatbot.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.firebaseHelpers.UsersFB;
import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.interfaces.UserInterFace;
import com.fyp.chatbot.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

public class UserViewModel extends ViewModel {
        MutableLiveData<UserModel> myUser = new MutableLiveData<>();
        MutableLiveData<Boolean> dataUpdated = new MutableLiveData<>();
        UserInterFace userInterFace = new UsersFB();
        public void setProfileUpdated(boolean updated) {
            dataUpdated.setValue(updated);
        }
        public LiveData<Boolean> getProfileUpdated() {
            return dataUpdated;
        }
        public LiveData<UserModel> getUser() {
            return myUser;
        }

        public void fetchUser() {
            String userId = FirebaseAuth.getInstance().getInstance().getUid();

            userInterFace.fetchUserProfile(userId,onResult -> {
                if (onResult != null) {
                    myUser.postValue(onResult);
                }
            });
        }
        public void updateImage(String userImage, FirebaseCallback<Boolean> onDataSave){
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null){
            userInterFace.setProfilePic(userId,userImage,onImageUpload -> {
                onDataSave.onComplete(onImageUpload);
            });
        }
    }

    }

