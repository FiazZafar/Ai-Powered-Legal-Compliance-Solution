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
import com.fyp.chatbot.activities.DocAnalyzer;
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

        binding.recyclerRecentDocs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRecentDocs.setAdapter(new RecentDocAdapter(docomentsList));


        binding.complianceCheck.setOnClickListener(view2 -> {
            getParentFragmentManager().beginTransaction().replace(R.id.container,new GenerateClauseFrag())
                .addToBackStack(null)
                .commit();
        });
        binding.summarizeReport.setOnClickListener(view3 -> {
            SummarizationFragment summarizationFragment = new SummarizationFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("Summarize_Report",true);
            summarizationFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.container,summarizationFragment)
                .addToBackStack(null)
                .commit();
        });
        binding.generateTemplate.setOnClickListener(view4 ->
                startActivity(new Intent(requireContext(),ContractGenerate.class)));
        return view;
    }
}