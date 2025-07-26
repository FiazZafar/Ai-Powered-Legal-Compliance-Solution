package com.fyp.chatbot.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.cloudinary.android.MediaManager;
import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.ActivityIntroBinding;
import com.google.firebase.FirebaseApp;
import java.util.HashMap;
import java.util.Map;

public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        setUpCloudinary();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.auth_container);
        NavController navController = navHostFragment.getNavController();

        binding.signUpBtn.setOnClickListener(v -> {
            binding.authContainer.setVisibility(VISIBLE);
            navController.navigate(R.id.signupFragment);
            binding.introConstraint.setVisibility(GONE);
        });
        binding.loginBtn.setOnClickListener(v -> {
            binding.authContainer.setVisibility(VISIBLE);
            navController.navigate(R.id.loginFragment);
            binding.introConstraint.setVisibility(GONE);

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
}