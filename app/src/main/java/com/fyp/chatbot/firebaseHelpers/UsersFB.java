package com.fyp.chatbot.firebaseHelpers;

import androidx.annotation.NonNull;
import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.interfaces.UserInterFace;
import com.fyp.chatbot.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UsersFB implements UserInterFace {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Smart_Goval")
            .child("Users");
    @Override
    public void addUser(UserModel userModel, String userId, FirebaseCallback<Boolean> result) {
        myRef.child(userModel.getUId()).setValue(userModel).addOnCompleteListener(task -> {
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