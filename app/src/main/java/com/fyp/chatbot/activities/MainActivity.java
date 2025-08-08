package com.fyp.chatbot.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.cloudinary.android.MediaManager;
import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.ActivityMainBinding;
import com.fyp.chatbot.fragments.SummarizationFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainNavFrag);
        navController = navHostFragment.getNavController();


        getIntents();
        setUpCloudinary();

        binding.bottomNav.setOnItemSelectedListener(view -> {
            int itemId = view.getItemId();
            if (itemId == R.id.homeTab){
                navController.navigate(R.id.homeFragment);
            } else if (itemId == R.id.analyzerTab) {
                startActivity(new Intent(this, DocAnalyzer.class));
            } else if (itemId == R.id.chatBot){
                startActivity(new Intent(this, ChatBot.class));
            } else if (itemId == R.id.profileTab) {
                navController.navigate(R.id.profileScreen);
            }else if (itemId == R.id.contractTab){
                startActivity(new Intent(this, ContractGenerate.class));
            }
            return false;
        });

        binding.chatBot.setOnClickListener(view -> startActivity(new Intent(this,ChatBot.class)));
    }

    private void setUpCloudinary() {

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dpdvhy4fx");
        config.put("api_key", "184645256189384");
        config.put("api_secret", "1t6EsEAX623sS0ZU0u1ln4cjRd8");

        try {
            MediaManager.get();
        } catch (IllegalStateException e) {
            MediaManager.init(this, config);
        }
    }

    private void getIntents() {
        Intent intent = getIntent();
        boolean isDocAnalyzer = intent.getBooleanExtra("DocAnalyzer",false);
        String taskType = intent.getStringExtra("TaskType");

        SummarizationFragment fragment = new SummarizationFragment();
        Bundle bundle = new Bundle();

        bundle.putString("TaskType",taskType);
        fragment.setArguments(bundle);
        if (isDocAnalyzer)
            navController.navigate(R.id.summarizationFragment,bundle);

    }
}

