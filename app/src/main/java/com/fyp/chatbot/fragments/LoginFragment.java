package com.fyp.chatbot.fragments;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.chatbot.activities.MainActivity;
import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.FragmentLoginBinding;
import com.fyp.chatbot.viewModels.SharedPreferenceViewModel;
import com.fyp.chatbot.viewModels.SignupViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private String userEmail,userPassword,userName,userImage;
    SignupViewModel signupViewModel;
    private static final int RC_SIGN_IN = 1001;
    SharedPreferenceViewModel viewModel;

    public LoginFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater,container,false);


        signupViewModel =  new ViewModelProvider(this,new ViewModelProvider
                .AndroidViewModelFactory(this.getActivity().getApplication()))
                .get(SignupViewModel.class);
        viewModel = new ViewModelProvider(this,new ViewModelProvider
                .AndroidViewModelFactory(requireActivity().getApplication()))
                .get(SharedPreferenceViewModel.class);


        visibilityListener();
        authListeners();
        initObservers();
        signInWithGoogle();
        return binding.getRoot();
    }
    private void signInWithGoogle(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        binding.googleBtn.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void initObservers() {
        signupViewModel.getValidationError().observe(getViewLifecycleOwner(), errorCode -> {
            setErrorsOnViews();
            if (errorCode != null) {
                switch (errorCode) {
                    case 1: binding.userName.setError("Name required"); break;
                    case 2: binding.emailTxt.setError("Invalid email"); break;
                    case 3: binding.passwordTxt.setError("Password too short"); break;
                }
            }
        });

        signupViewModel.getLoginStatus().observe(getViewLifecycleOwner(), onLogin->{
            binding.linearlayout2.setVisibility(GONE);
            if (onLogin){
                Toast.makeText(this.getActivity(), "Login Successful...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this.getActivity(), MainActivity.class));
                this.getActivity().finish();
            }else{
//                Toast.makeText(this.getActivity(), "Login Failed...", Toast.LENGTH_SHORT).show();
            }
        });


        signupViewModel.getGoogleSignInStatus().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {

                Toast.makeText(getContext(), "Signed in with Google successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), MainActivity.class));
                this.getActivity().finish();

            } else {
                Toast.makeText(getContext(), "Google Sign-In failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authListeners() {
        binding.loginBtn.setOnClickListener(view -> {
            userName = binding.userName.getText().toString().trim();
            userEmail = binding.emailTxt.getText().toString().trim();
            userPassword = binding.passwordTxt.getText().toString().trim(); // fixed

            if (signupViewModel.validateInputs(userName,userEmail,userPassword,userPassword)) {
                binding.linearlayout2.setVisibility(View.VISIBLE);
                signupViewModel.loginUser(userEmail,userPassword,userImage,userName);
                viewModel.savedData(userName,userEmail,"");

            } else {
                Toast.makeText(requireActivity(), "Invalid inputs...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.signUpBtn.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.signupFragment);
        });
        binding.forgetPasswordBtn.setOnClickListener(view -> {
            userEmail = binding.emailTxt.getText().toString().trim();
            if (!userEmail.isEmpty()) {
                signupViewModel.forgetPassword(userEmail).observe(getViewLifecycleOwner(), isSuccess -> {
                    if (isSuccess) {
                        Toast.makeText(requireContext(), "Reset link sent! Check your email.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to send reset email. Try again.", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Enter your email, please...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void visibilityListener() {
        binding.eyeVisibleconfirmBTn.setOnClickListener(view -> {
            if (binding.passwordTxt.getInputType() == (InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                if (!binding.passwordTxt.getText().toString().equals("")){
                    binding.passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT
                            |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.eyeVisibleconfirmBTn.setImageResource(R.drawable.visibility_off_24px);
                }
            }else {
                if (!binding.passwordTxt.getText().toString().equals("")) {
                    binding.passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT|
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.eyeVisibleconfirmBTn.setImageResource(R.drawable.visibility_24px);
                }
            }
        });


    }
    private void setErrorsOnViews() {
        binding.passwordTxt.setError(null);
        binding.passwordTxt.setError(null);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                 userName = account.getDisplayName();
                 userEmail = account.getEmail();
                 userImage = account.getPhotoUrl() != null ?
                        account.getPhotoUrl().toString() : "";

                viewModel.savedData(userName,userEmail,userImage);
                signupViewModel.signInWithGoogle(account,account.getIdToken());

            } catch (ApiException e) {
                Toast.makeText(getContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}