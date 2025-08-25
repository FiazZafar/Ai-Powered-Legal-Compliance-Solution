package com.fyp.chatbot.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fyp.chatbot.R;
import com.fyp.chatbot.activities.ClauseHistory;
import com.fyp.chatbot.activities.DocsHistory;
import com.fyp.chatbot.adapters.RecentDocAdapter;
import com.fyp.chatbot.databinding.FragmentHomeBinding;
import com.fyp.chatbot.models.DocomentModel;
import com.fyp.chatbot.viewModels.SharedPreferenceViewModel;
import com.fyp.chatbot.viewModels.UserViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding ;
    private List<DocomentModel> docomentModelList;
    private RecentDocAdapter adapter;
    SharedPreferenceViewModel viewModel;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this,new ViewModelProvider
                .AndroidViewModelFactory(this.getActivity().getApplication()))
                .get(SharedPreferenceViewModel.class);


        viewModel.getData().observe(getViewLifecycleOwner(),userModel -> {
            if (userModel != null){
                if (!userModel.getUserName().isEmpty()){
                    Log.d("HomeFrag", "onCreateView: name is " + userModel.getUserName());
                    Log.d("HomeFrag", "onCreateView: email is " + userModel.getUserEmail());
                    binding.userName.setText(userModel.getUserName());
                }
                if (!userModel.getImgUrl().isEmpty())
                    Glide.with(this.getContext()).load(userModel.getImgUrl())
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .into(binding.userProfile);
            }
        });

        docomentModelList = new ArrayList<>();
        adapter = new RecentDocAdapter(docomentModelList);

        getSavedFiles();
        setListeners();

        binding.recyclerRecentDocs.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerRecentDocs.setAdapter(adapter);

        return view;
    }
    void setListeners(){
        binding.savedClauses.setOnClickListener(view1 -> {
            startActivity(new Intent(this.getContext(), ClauseHistory.class));
        });
        binding.summarizeReport.setOnClickListener(view3 -> {

            Bundle bundle = new Bundle();
            bundle.putString("TaskType","Contract Summarizer");

            NavController navController = Navigation.findNavController(view3);
            navController.navigate(R.id.action_homeFragment_to_summarizationFragment,bundle);
        });
        binding.generateClause.setOnClickListener(view4 ->{
            NavController navController = Navigation.findNavController(view4);
            navController.navigate(R.id.action_homeFragment_to_generateClauseFrag);
        });
        binding.complianceCheck.setOnClickListener(view5 -> {
            Bundle bundle = new Bundle();
            bundle.putString("TaskType","Compliance Checker");
            NavController navController = Navigation.findNavController(view5);
            navController.navigate(R.id.action_homeFragment_to_summarizationFragment,bundle);
        });
        binding.viewAll.setOnClickListener(view6 -> {
            startActivity(new Intent(this.getContext(), DocsHistory.class));} );


    }
    private void getSavedFiles() {
        docomentModelList.clear(); // Clear old list
        File fileDir = requireContext().getFilesDir();
        File[] allFiles = fileDir.listFiles();

        if (allFiles != null && allFiles.length > 0) {
            for (File file : allFiles) {
                if (file.getName().startsWith("Smart_Goval_") && file.getName().endsWith(".pdf")) {
                    try {
                        String namePart = file.getName()
                                .replace("Smart_Goval_", "")
                                .replace(".pdf", "");
                        String[] parts = namePart.split("_");
                        if (parts.length >= 2) {
                            String name = parts[0];
                            String timestampStr = parts[1];

                            long timestamp = Long.parseLong(timestampStr);
                            String formattedTime = formatTimestamp(timestamp);

                            docomentModelList.add(new DocomentModel(name, formattedTime));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (docomentModelList.isEmpty()) {
                binding.recyclerRecentDocs.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerRecentDocs.setVisibility(View.VISIBLE);
                binding.emptyView.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                binding.recyclerRecentDocs.scrollToPosition(docomentModelList.size() - 1);
            }

        } else {
            binding.recyclerRecentDocs.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        }
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

}