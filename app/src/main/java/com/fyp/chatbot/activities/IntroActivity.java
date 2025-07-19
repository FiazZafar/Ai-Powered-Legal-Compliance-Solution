package com.fyp.chatbot.activities;

import static android.view.View.GONE;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cloudinary.android.MediaManager;
import com.fyp.chatbot.databinding.ActivityIntroBinding;
import com.fyp.chatbot.fragments.LoginFragment;
import com.fyp.chatbot.fragments.SignupFragment;
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
        binding.signUpBtn.setOnClickListener(v -> {
            loadFragment(new SignupFragment(),1);
            binding.introConstraint.setVisibility(GONE);

        });
        binding.loginBtn.setOnClickListener(v -> {
            loadFragment(new LoginFragment(),2);
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
    private void loadFragment(Fragment fragment, int flag) {
        if (isFinishing() || isDestroyed()) return;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        int containerId = binding.authContainer.getId();
        String tag = fragment.getClass().getSimpleName();

        if (flag == 1) {
            ft.add(containerId, fragment, tag);
        } else if (flag == 2) {

            ft.replace(containerId, fragment, tag);
            ft.addToBackStack(tag);
        } else if (flag == 3) {
            ft.add(containerId, fragment, tag);
            ft.addToBackStack(tag);
        }

        ft.commitAllowingStateLoss();
    }
}