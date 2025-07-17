package com.fyp.chatbot.firebaseHelpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.interfaces.UserInterFace;
import com.fyp.chatbot.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFB implements UserInterFace {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("QuickChat")
            .child("Users");
    @Override
    public void addUser(UserModel userModel, String userId, FirebaseCallback<Boolean> result) {
        myRef.child(userModel.getuId()).setValue(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                result.onComplete(true);
            }
        });
    }
    @Override
    public void setProfilePic(String userId,String profilePic,FirebaseCallback<Boolean> result) {

        myRef.child(userId).child("imgUrl").setValue(profilePic).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    result.onComplete(true);
                }
            }
        });
    }
    @Override
    public void setOnlineStatus(String userId, Boolean isOnline, FirebaseCallback<Boolean> result) {
        myRef.child(userId).child("online")
                .setValue(isOnline).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        result.onComplete(true);
                    }else {
                        result.onComplete(false);
                    }
                });
    }


    @Override
    public void fetchUsersList(FirebaseCallback<List<UserModel>> result) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<UserModel> userList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot mySnapshot : snapshot.getChildren()) {
                        UserModel userModel = mySnapshot.getValue(UserModel.class);
                        if (userModel != null && userModel.getUserName() != null &&
                                userModel.getuId() != null && !userModel.getuId().trim().isEmpty()
                                && !userId.equals(userModel.getuId().trim())) {
                            userList.add(userModel);
                        } else {
                        }
                    }
                    result.onComplete(userList);
                }else {
                    result.onComplete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fetchUsersList", "Cancelled: ", error.toException());
            }
        });
    }


    @Override
    public void fetchUserProfile(String userId, FirebaseCallback<UserModel> result) {
        myRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null){
                        result.onComplete(userModel);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}