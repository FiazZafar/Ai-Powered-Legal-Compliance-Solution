package com.fyp.chatbot.firebaseHelpers;


import android.util.Log;

import androidx.annotation.NonNull;

import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.interfaces.SignupInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupFB implements SignupInterface {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Smart_Goval")
            .child("Users");
    DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Smart_Goval")
            .child("Device-Tokens");

    @Override
    public void signupUser(String email, String passwords, FirebaseCallback<Boolean> result) {
        mAuth.createUserWithEmailAndPassword(email.trim(), passwords.trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.onComplete(true);
                    } else {
                        result.onComplete(false);
                    }
                })
                .addOnFailureListener(e -> {
                    result.onComplete(false);
                });
    }
    @Override
    public void loginUser(String email, String passwords,FirebaseCallback<Boolean> result) {
        Log.e("LoginFB", "signupUser: Login Start");
        mAuth.signInWithEmailAndPassword(email.trim(),passwords.trim()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.e("LoginFB", "signupUser: Login success");
                result.onComplete(true);
            }else {
                Log.e("LoginFB", "signupUser: Login failed");
                Log.e("LoginFB", "Login failed: " + task.getException().getMessage());
                result.onComplete(false);
            }
        });
    }
    @Override
    public void firebaseAuthWithGoogle(String idToken, FirebaseCallback<Boolean> result) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignupFB", "firebaseAuthWithGoogle: success");
                        result.onComplete(true);
                    } else {
                        Log.e("SignupFB", "firebaseAuthWithGoogle: failure", task.getException());
                        result.onComplete(false);
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
    public void resetPassword(String email, FirebaseCallback<Boolean> result) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ForgetPassword", "Email sent successfully");
                        result.onComplete(true);
                    } else {
                        Log.e("ForgetPassword", "Failed: " + task.getException().getMessage());
                        result.onComplete(false);
                    }
                });
    }

}
