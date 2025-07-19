package com.fyp.chatbot.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fyp.chatbot.adapters.DocAnalyzerAdapter;
import com.fyp.chatbot.databinding.ActivityDocAnalyzerBinding;

import java.util.Arrays;
import java.util.List;

public class DocAnalyzer extends AppCompatActivity {

    ActivityDocAnalyzerBinding binding;
    List<String> docList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocAnalyzerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        docList = Arrays.asList("Contract Summarizer","Risk Clause Detector",
                "Confidentiality Clause Tracker","Jurisdiction Identifier",
                "Compliance Checker", "Obligation Extractor");

        binding.analyzeDocRec.setLayoutManager(new GridLayoutManager(this,2));
        binding.analyzeDocRec.setAdapter(new DocAnalyzerAdapter(docList,this));

        binding.backBtn.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}