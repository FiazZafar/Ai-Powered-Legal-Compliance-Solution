package com.fyp.chatbot.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.chatbot.MainActivity;
import com.fyp.chatbot.databinding.ActivityIntroBinding;
import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
        }else {

            binding.signUpBtn.setOnClickListener(v ->startActivity(new Intent(this, SignupActivity.class)));
            binding.loginBtn.setOnClickListener(v ->startActivity(new Intent(this, LoginActivity.class)));

        }
    }
}