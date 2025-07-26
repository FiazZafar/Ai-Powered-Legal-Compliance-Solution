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

        getIntents();
        setUpCloudinary();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainNavFrag);
        navController = navHostFragment.getNavController();

        binding.bottomNav.setOnItemSelectedListener(view -> {
            int itemId = view.getItemId();
            if (itemId == R.id.homeTab){
                navController.navigate(R.id.homeFragment);
            }else if (itemId == R.id.analyzerTab){
                navController.navigate(R.id.summarizationFragment);
            } else if (itemId == R.id.profileTab) {
                navController.navigate(R.id.profileScreen);
            }else if (itemId == R.id.contractTab){
                startActivity(new Intent(this, ContractGenerate.class));
            }
            return false;
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

//        bundle.putBoolean("DocAnalyzer",isDocAnalyzer);
//        bundle.putString("TaskType",taskType);
//        fragment.setArguments(bundle);
//        if (isDocAnalyzer)
//            loadFragment(fragment,1);
//        else
//            loadFragment(new HomeFragment(),0);
    }

//    public void loadFragment(Fragment fragment, int flag){
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//
//        if (flag == 0){
//            ft.add(R.id.container,fragment);
//        }else {
//            ft.replace(R.id.container,fragment);
//        }
//        ft.commit();
//    }
}

