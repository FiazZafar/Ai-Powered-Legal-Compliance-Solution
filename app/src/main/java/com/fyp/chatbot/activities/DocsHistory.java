package com.fyp.chatbot.activities;

import android.os.Bundle;
import android.view.View;
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
    private static boolean isNull = true;
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

        getSavedLegalDoc();
        binding.noResultFound.setVisibility(isNull ? View.VISIBLE : View.GONE);
        binding.recyclerRecentDocs.setVisibility(isNull ? View.GONE : View.VISIBLE);
    }

    private void getSavedLegalDoc() {
        File fileDir = getFilesDir();
        File [] allFiles = fileDir.listFiles();
        binding.progressCircular.setVisibility(View.GONE);


        if (allFiles != null) {
            for (File file : allFiles){
                if (file.getName().startsWith("Smart_Goval_") && file.getName().endsWith(".pdf")) {
                    isNull = false;
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