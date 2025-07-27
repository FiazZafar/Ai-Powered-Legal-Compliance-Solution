package com.fyp.chatbot.activities;

import static android.view.View.GONE;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fyp.chatbot.adapters.RecentDocAdapter;
import com.fyp.chatbot.databinding.ActivityDocsHistoryBinding;
import com.fyp.chatbot.models.DocomentModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocsHistory extends AppCompatActivity {
    ActivityDocsHistoryBinding binding;
    private List<DocomentModel> docomentModelList;
    private RecentDocAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocsHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        docomentModelList = new ArrayList<>();
        adapter = new RecentDocAdapter(docomentModelList);

        binding.backBtn.setOnClickListener(view -> finish());

        binding.recyclerRecentDocs.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRecentDocs.setAdapter(adapter);
        binding.linearlayout2.setVisibility(GONE);

        getSavedLegalDoc();

    }

    private void getSavedLegalDoc() {
        File fileDir = getFilesDir();
        File [] allFiles = fileDir.listFiles();


        if (allFiles != null){
            for (File file : allFiles){
                if (file.getName().startsWith("Smart_Goval_") && file.getName().endsWith(".pdf")) {
                    try {
                        String namePart = file.getName()
                                .replace("Smart_Goval_", "")
                                .replace(".pdf", "");
                        String [] parts = namePart.split("_");
                        String name = parts[0];
                        String timestampStr = parts[1];

                        long timestamp = Long.parseLong(timestampStr);
                        String formattedTime = formatTimestamp(timestamp);

                        docomentModelList.add(new DocomentModel(name, formattedTime));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                }
            }
        }
        private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}