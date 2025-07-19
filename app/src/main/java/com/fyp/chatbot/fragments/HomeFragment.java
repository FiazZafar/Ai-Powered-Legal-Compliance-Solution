package com.fyp.chatbot.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fyp.chatbot.R;
import com.fyp.chatbot.activities.ClauseHistory;
import com.fyp.chatbot.activities.DocsHistory;
import com.fyp.chatbot.adapters.RecentDocAdapter;
import com.fyp.chatbot.databinding.FragmentHomeBinding;
import com.fyp.chatbot.models.Docoments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding ;
    private List<Docoments> docomentsList;
    private RecentDocAdapter adapter ;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        docomentsList = new ArrayList<>();
        adapter = new RecentDocAdapter(docomentsList);
        getSavedFiles();

        binding.recyclerRecentDocs.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerRecentDocs.setAdapter(adapter);


        binding.savedClauses.setOnClickListener(view1 -> {
            startActivity(new Intent(this.getContext(), ClauseHistory.class));
        });
        binding.summarizeReport.setOnClickListener(view3 -> {
            SummarizationFragment summarizationFragment = new SummarizationFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("Summarize_Report",true);
            bundle.putString("TaskType","Contract Summarizer");
            summarizationFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.container,summarizationFragment)
                .addToBackStack(null)
                .commit();
        });
        binding.generateClause.setOnClickListener(view4 ->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, new GenerateClauseFrag())
                    .addToBackStack(null)
                    .commit();
        });
        binding.complianceCheck.setOnClickListener(view5 -> {
            SummarizationFragment summarizationFragment = new SummarizationFragment();
            Bundle bundle = new Bundle();
//            bundle.putBoolean("Compliance_check",true);
            bundle.putString("TaskType","Compliance Checker");
            summarizationFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.container,summarizationFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.viewAll.setOnClickListener(view6 -> {
            startActivity(new Intent(this.getContext(), DocsHistory.class));} );
        return view;
    }

    private void getSavedFiles() {
        File fileDir = requireContext().getFilesDir();
        File[] allFiles = fileDir.listFiles();

        if (allFiles != null) {
            for (File file : allFiles) {
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

                        docomentsList.add(new Docoments(name, formattedTime));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Notify adapter after loop
            adapter.notifyDataSetChanged();
            binding.recyclerRecentDocs.scrollToPosition(docomentsList.size() - 1);

        } else {
            Toast.makeText(requireContext(), "No files found", Toast.LENGTH_SHORT).show();
        }
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

}