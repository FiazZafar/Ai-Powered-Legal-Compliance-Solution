package com.fyp.chatbot.fragments;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.chatbot.R;
import com.fyp.chatbot.activities.About;
import com.fyp.chatbot.activities.ClauseHistory;
import com.fyp.chatbot.activities.DocsHistory;
import com.fyp.chatbot.activities.IntroActivity;
import com.fyp.chatbot.databinding.FragmentProfileScreenBinding;
import com.fyp.chatbot.viewModels.SharedPreferenceViewModel;
import com.fyp.chatbot.viewModels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileScreen extends Fragment {

    private final int REQUEST_CODE_IMAGE = 101;
    FragmentProfileScreenBinding binding;
    UserViewModel userViewModel;
    SharedPreferenceViewModel viewModel;
    String helpCenter,termConditions,privacyPolicy;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileScreenBinding.inflate(inflater, container, false);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        viewModel = new ViewModelProvider(this,new ViewModelProvider
                .AndroidViewModelFactory(this.getActivity().getApplication()))
                .get(SharedPreferenceViewModel.class);

        setUpVariables();
        initListeners();
        initObserver();
        return binding.getRoot();
    }

    private void setUpVariables() {
        privacyPolicy = "Privacy Policy\n\n" +
                "We value your trust and take your privacy seriously. This AI-powered Legal & Compliance " +
                "Solution is designed to help individuals and businesses with legal documentation and compliance " +
                "advice, while ensuring that your data remains secure.\n\n" +
                "1. Information We Collect:\n" +
                "- Personal Information (name, email, contact details) only when voluntarily provided.\n" +
                "- Usage Data (device type, IP address, app activity) for improving user experience.\n" +
                "- Legal Document Information, stored securely to provide accurate recommendations.\n\n" +
                "2. How We Use Your Information:\n" +
                "- To provide legal and compliance insights tailored to your needs.\n" +
                "- To improve app performance and provide new features.\n" +
                "- To ensure data security and meet applicable legal regulations.\n\n" +
                "3. Data Security:\n" +
                "We use encryption, secure servers, and access control measures to protect your data. We do not " +
                "share or sell your information to third parties without consent, except as required by law.\n\n" +
                "4. Your Rights:\n" +
                "- You have the right to request data deletion.\n" +
                "- You can opt out of marketing communications anytime.\n" +
                "- You may request copies of your stored legal documents.\n\n" +
                "5. Updates:\n" +
                "We may update this Privacy Policy periodically. Please review it regularly to stay informed.\n\n" +
                "By using this application, you consent to our data practices outlined in this policy.";

        termConditions = "Terms and Conditions\n\n" +
                "Welcome to the AI-powered Legal & Compliance Solution. By accessing or using this application, " +
                "you agree to these terms. Please read them carefully.\n\n" +
                "1. Acceptance of Terms:\n" +
                "Your continued use of the application indicates your acceptance of these terms. If you do not agree, " +
                "please discontinue using the app.\n\n" +
                "2. Services Provided:\n" +
                "This app offers AI-powered tools for legal documentation, compliance guidance, and contract reviews. " +
                "All outputs are for informational purposes only and do not substitute professional legal counsel.\n\n" +
                "3. User Responsibilities:\n" +
                "- Provide accurate details to ensure reliable compliance recommendations.\n" +
                "- Do not use the application for illegal purposes or unethical practices.\n" +
                "- Maintain confidentiality of your login credentials.\n\n" +
                "4. Intellectual Property:\n" +
                "All app content, including AI models, documentation, and branding, is the property of the developers " +
                "and protected under copyright laws.\n\n" +
                "5. Limitation of Liability:\n" +
                "We are not liable for any losses, damages, or consequences resulting from reliance on automated " +
                "legal advice. Users are encouraged to seek professional counsel when needed.\n\n" +
                "6. Termination:\n" +
                "We reserve the right to suspend or terminate access for users violating these terms.\n\n" +
                "7. Modifications:\n" +
                "These terms may change with updates. Please review them regularly.\n\n" +
                "By continuing to use this app, you confirm that you have read, understood, and agreed to these terms.";

        helpCenter = "Help Center\n\n" +
                "Welcome to the Help Center of the AI-powered Legal & Compliance Solution. We aim to make navigating " +
                "legal complexities simple and efficient. Below are some guidelines and resources to help you:\n\n" +
                "1. Getting Started:\n" +
                "- Create your account to access personalized legal tools.\n" +
                "- Upload or draft documents for AI-assisted reviews.\n\n" +
                "2. Common Features:\n" +
                "- AI Document Review: Get instant feedback on contracts.\n" +
                "- Compliance Audit: Understand legal risks and regulatory requirements.\n" +
                "- Document Templates: Access pre-built legal templates.\n\n" +
                "3. Frequently Asked Questions:\n" +
                "Q: Is my data safe?\n" +
                "A: Yes, we use encryption and secure servers to protect your documents.\n\n" +
                "Q: Can I use this app for personal and business needs?\n" +
                "A: Absolutely! The app supports individuals, startups, and enterprises.\n\n" +
                "Q: Does this replace a lawyer?\n" +
                "A: No, this app is an assistant tool. It provides suggestions, not legal representation.\n\n" +
                "4. Support:\n" +
                "If you encounter issues, contact our support team at: fiazzafar430@gmail.com\n\n" +
                "5. Feedback:\n" +
                "We value your feedback to improve our AI tools. Send suggestions through the in-app feedback form.";


    }

    private void initObserver() {
        viewModel.getData().observe(getViewLifecycleOwner(),userModel -> {
            if (userModel != null){
                if (!userModel.getUserName().isEmpty()){
                    binding.userName.setText(userModel.getUserName());
                }
                if (!userModel.getImgUrl().isEmpty())
                    Glide.with(this.getContext()).load(userModel.getImgUrl())
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .into(binding.userProfile);

                if (!userModel.getUserEmail().isEmpty()){
                    binding.userEmail.setText(userModel.getUserEmail());
                }
            }
        });
    }
    private void initListeners() {


        binding.helpCenter.setOnClickListener(v -> openContent("Help Center", helpCenter));
        binding.termCondition.setOnClickListener(v -> openContent("Terms & Conditions", termConditions));
        binding.privacyPolicy.setOnClickListener(v -> openContent("Privacy Policy", privacyPolicy));


        binding.clauseHistory.setOnClickListener(view -> {
            startActivity(new Intent(this.getContext(), ClauseHistory.class));
        });
        binding.docHistory.setOnClickListener(view -> {
            startActivity(new Intent(this.getContext(), DocsHistory.class));
        });

        binding.uploadProfile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_IMAGE);
            binding.linearlayout2.setVisibility(VISIBLE);
        });

        binding.logoutBtn.setOnClickListener(view -> {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), IntroActivity.class));

        });
    }

    private void openContent(String title, String content) {
        Intent intent = new Intent(this.getContext(), About.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                viewModel.uploadToCloudinary(uri,onCallback -> {
                    binding.userProfile.setImageURI(uri);
                    binding.linearlayout2.setVisibility(GONE);
                    Toast.makeText(this.getContext(), "Image updated", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}