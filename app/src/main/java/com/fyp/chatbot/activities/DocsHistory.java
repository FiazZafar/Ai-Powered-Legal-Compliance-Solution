package com.fyp.chatbot.activities;

import static android.view.View.GONE;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fyp.chatbot.R;
import com.fyp.chatbot.adapters.RecentDocAdapter;
import com.fyp.chatbot.databinding.ActivityDocsHistoryBinding;
import com.fyp.chatbot.models.Docoments;

import java.util.ArrayList;
import java.util.List;

public class DocsHistory extends AppCompatActivity {

    ActivityDocsHistoryBinding binding;
    private List<Docoments> docomentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        docomentsList = new ArrayList<>();
        docomentsList.add(new Docoments("doc.pdf",String.valueOf(System.currentTimeMillis())));
        docomentsList.add(new Docoments("doc.pdf",String.valueOf(System.currentTimeMillis())));
        docomentsList.add(new Docoments("doc.pdf",String.valueOf(System.currentTimeMillis())));
        docomentsList.add(new Docoments("doc.pdf",String.valueOf(System.currentTimeMillis())));
        binding.recyclerRecentDocs.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRecentDocs.setAdapter(new RecentDocAdapter(docomentsList));
        binding.linearlayout2.setVisibility(GONE);
    }
}