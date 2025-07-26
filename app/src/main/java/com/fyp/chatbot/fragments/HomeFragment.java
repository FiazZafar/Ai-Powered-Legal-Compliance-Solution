package com.fyp.chatbot.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

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
    private RecentDocAdapter adapter ;
    private UserViewModel userViewModel;
    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.fetchUser();

        initObserver();


        docomentModelList = new ArrayList<>();
        adapter = new RecentDocAdapter(docomentModelList);

        getSavedFiles();
        setListeners();

        binding.recyclerRecentDocs.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerRecentDocs.setAdapter(adapter);

        return view;
    }

    private void initObserver() {
        userViewModel.getUser().observe(getViewLifecycleOwner(),onResult -> {

            if(onResult != null){
                String userName = onResult.getUserName();
                String userImage = onResult.getImgUrl();
                if (userName != null)
                    binding.userName.setText(userName);
                if (userImage != null)
                    Glide.with(this.getContext()).load(userImage)
                            .placeholder(R.drawable.account_circle_24px)
                            .error(R.drawable.account_circle_24px)
                            .into(binding.userProfile);
            }
        });
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