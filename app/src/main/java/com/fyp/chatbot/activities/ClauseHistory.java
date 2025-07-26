package com.fyp.chatbot.activities;

import static android.view.View.GONE;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fyp.chatbot.adapters.ClauseHistoryAdapter;
import com.fyp.chatbot.databinding.ActivityClauseHistoryBinding;
import com.fyp.chatbot.models.ClauseModel;
import com.fyp.chatbot.viewModels.ClauseViewModel;

import java.util.ArrayList;
import java.util.List;

public class ClauseHistory extends AppCompatActivity {

    ActivityClauseHistoryBinding binding;
    List<ClauseModel> clauseList ;
    ClauseViewModel clauseMVVM;
    ClauseHistoryAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityClauseHistoryBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());
       clauseMVVM = new ViewModelProvider(this).get(ClauseViewModel.class);
       clauseMVVM.setClauseList();
       clauseList = new ArrayList<>();

       adapter = new ClauseHistoryAdapter(clauseList,this);
       initViews();
       initObservers();

    }

    private void initObservers() {
        clauseMVVM.getClauseList().observe(this,onClauseList -> {
            binding.linearlayout2.setVisibility(GONE);
            if (onClauseList != null && !onClauseList.isEmpty()){
                adapter.updateList(onClauseList);
            }
        });
    }

    private void initViews() {
        binding.clauseHistoryRec.setLayoutManager(new LinearLayoutManager(this));
        binding.clauseHistoryRec.setAdapter(adapter);
    }
}