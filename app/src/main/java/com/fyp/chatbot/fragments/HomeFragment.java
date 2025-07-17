package com.fyp.chatbot.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fyp.chatbot.ContractGenerate;
import com.fyp.chatbot.MainActivity;
import com.fyp.chatbot.R;
import com.fyp.chatbot.activities.ClauseHistory;
import com.fyp.chatbot.activities.DocAnalyzer;
import com.fyp.chatbot.activities.DocsHistory;
import com.fyp.chatbot.adapters.RecentDocAdapter;
import com.fyp.chatbot.databinding.FragmentHomeBinding;
import com.fyp.chatbot.models.Docoments;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding ;
    private List<Docoments> docomentsList;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        docomentsList = new ArrayList<>();
        docomentsList.add(new Docoments("doc.pdf",String.valueOf(System.currentTimeMillis())));
        docomentsList.add(new Docoments("doc.pdf",String.valueOf(System.currentTimeMillis())));
        docomentsList.add(new Docoments("doc.pdf",String.valueOf(System.currentTimeMillis())));
        docomentsList.add(new Docoments("doc.pdf",String.valueOf(System.currentTimeMillis())));

        binding.recyclerRecentDocs.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerRecentDocs.setAdapter(new RecentDocAdapter(docomentsList));


        binding.complianceCheck.setOnClickListener(view2 -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container,new ComplianceCheck())
                .addToBackStack(null)
                .commit();
        });
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
            bundle.putBoolean("Compliance_check",true);
            bundle.putString("TaskType","Compliance Checker");
            summarizationFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.container,summarizationFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.viewAll.setOnClickListener(view6 -> {
            startActivity(new Intent(this.getContext(), DocsHistory.class));
        } );
        return view;
    }
}