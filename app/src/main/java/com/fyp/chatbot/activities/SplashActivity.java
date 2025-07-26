package com.fyp.chatbot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.chatbot.databinding.ActivitySplashBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (user != null){
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }else {
                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                finish();
            }
        },3000);
    }
}