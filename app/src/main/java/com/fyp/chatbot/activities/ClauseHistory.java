package com.fyp.chatbot.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fyp.chatbot.R;
import com.fyp.chatbot.adapters.ClauseHistoryAdapter;
import com.fyp.chatbot.adapters.DocAnalyzerAdapter;
import com.fyp.chatbot.databinding.ActivityClauseHistoryBinding;

public class ClauseHistory extends AppCompatActivity {

    ActivityClauseHistoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityClauseHistoryBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());
       binding.clauseHistoryRec.setLayoutManager(new LinearLayoutManager(this));
        binding.clauseHistoryRec.setAdapter(new ClauseHistoryAdapter());
    }
}