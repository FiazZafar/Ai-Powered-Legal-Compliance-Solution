package com.fyp.chatbot.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.ActivityAboutBinding;

public class About extends AppCompatActivity {

    ActivityAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");

        Log.d("About", "onCreate: title is " + title);
        Log.d("About", "onCreate: content is " + content);

        binding.backBtn.setOnClickListener(view -> onBackPressed());
        if (!title.isEmpty()){
            binding.contentTitle.setText(title);
        }
        if (!content.isEmpty()){
            binding.contentText.setText(content);
        }
    }
}