package com.fyp.chatbot;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cloudinary.android.MediaManager;
import com.fyp.chatbot.activities.DocAnalyzer;
import com.fyp.chatbot.databinding.ActivityMainBinding;
import com.fyp.chatbot.fragments.HomeFragment;
import com.fyp.chatbot.fragments.ProfileScreen;
import com.fyp.chatbot.fragments.SummarizationFragment;
import com.fyp.chatbot.models.Docoments;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntents();
        setUpCloudinary();





        binding.chatBotBtn.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, ChatBot.class)));
        binding.dashboardBtnConstraint.setOnClickListener(view ->
                    loadFragment(new HomeFragment(),1));
        binding.generatePdfBtnConstraint.setOnClickListener(view ->
                    startActivity(new Intent(MainActivity.this,ContractGenerate.class)));
        binding.profilePicConstraint.setOnClickListener(view ->
                    loadFragment(new ProfileScreen(),1));
        binding.analyzeContractContraints.setOnClickListener(view -> {
            startActivity(new Intent(this, DocAnalyzer.class));
        });

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

        bundle.putBoolean("DocAnalyzer",isDocAnalyzer);
        bundle.putString("TaskType",taskType);
        fragment.setArguments(bundle);
        if (isDocAnalyzer)
            loadFragment(fragment,1);
        else
            loadFragment(new HomeFragment(),0);
    }

    public void loadFragment(Fragment fragment, int flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (flag == 0){
            ft.add(R.id.container,fragment);
        }else {
            ft.replace(R.id.container,fragment);
        }
        ft.commit();
    }

}

