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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.fyp.chatbot.activities.MainActivity;
import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.FragmentSignupBinding;
import com.fyp.chatbot.viewModels.SharedPreferenceViewModel;
import com.fyp.chatbot.viewModels.SignupViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignupFragment extends Fragment {
    private FragmentSignupBinding binding;
    private String userName,userEmail,passwords,confirmPass,userImage;
    private final int REQUEST_CODE_IMAGE = 101;
    private static final int RC_SIGN_IN = 1001;
    SignupViewModel signupViewModel;
    SharedPreferenceViewModel viewModel;

    public SignupFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater,container,false);

        signupViewModel =  new ViewModelProvider(this,new ViewModelProvider
                .AndroidViewModelFactory(this.getActivity().getApplication()))
                .get(SignupViewModel.class);
        viewModel = new ViewModelProvider(this,new ViewModelProvider
                .AndroidViewModelFactory(this.getActivity().getApplication()))
                .get(SharedPreferenceViewModel.class);

        initObservers();
        generalListeners();
        visibilityListeners();
        authListeners();

        signInWithGoogle();
        return  binding.getRoot();
    }
    private void signInWithGoogle(){
        signupViewModel.getGoogleSignInIntent().observe(getViewLifecycleOwner(),intent -> {
            if (intent != null)
                startActivityForResult(intent, RC_SIGN_IN);
        });

        binding.googleBtn.setOnClickListener(v -> {
            signupViewModel.setGoogleSignInIntent(String.valueOf(R.string.default_web_client_id));
        });
    }
    private void visibilityListeners() {
        binding.eyeVisiblePasBTn.setOnClickListener(view -> {
            if (binding.passwordTxt.getInputType() == (InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                if (!binding.passwordTxt.getText().toString().equals("")){
                    binding.passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT
                            |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.eyeVisiblePasBTn.setImageResource(R.drawable.visibility_off_24px);
                }
            }else {
                if (!binding.passwordTxt.getText().toString().equals("")) {
                    binding.passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT|
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.eyeVisiblePasBTn.setImageResource(R.drawable.visibility_24px);
                }
            }
        });

        binding.eyeVisibleconfirmBTn.setOnClickListener(view -> {
            if (binding.confirmPasswordTxt.getInputType() == (InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                if (!binding.confirmPasswordTxt.getText().toString().equals("")){
                    binding.confirmPasswordTxt.setInputType(InputType.TYPE_CLASS_TEXT
                            |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.eyeVisibleconfirmBTn.setImageResource(R.drawable.visibility_off_24px);
                }
            }else {
                if (!binding.confirmPasswordTxt.getText().toString().equals("")){
                    binding.confirmPasswordTxt.setInputType(InputType.TYPE_CLASS_TEXT|
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.eyeVisibleconfirmBTn.setImageResource(R.drawable.visibility_24px);
                }
            }
        });
    }
    private void generalListeners() {
        binding.uploadProfile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_IMAGE);
        });
    }

    private void setErrorsOnViews() {
        binding.nameTxt.setError(null);
        binding.emailTxt.setError(null);
        binding.passwordTxt.setError(null);
        binding.confirmPasswordTxt.setError(null);
    }
    private void authListeners() {
        binding.loginBtn.setOnClickListener(view -> {
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.loginFragment);
                });

        binding.signUpBtn.setOnClickListener(view -> {
            userName = binding.nameTxt.getText().toString().trim();
            userEmail = binding.emailTxt.getText().toString().trim();
            passwords = binding.passwordTxt.getText().toString().trim();
            confirmPass = binding.confirmPasswordTxt.getText().toString().trim();

            if (signupViewModel.validateInputs(userName,userEmail,passwords,confirmPass)){
                binding.linearlayout2.setVisibility(VISIBLE);
                signupViewModel.registerUser(userEmail,passwords,userName,userImage);
                viewModel.savedData(userName,userEmail,"");
            }else {
                Toast.makeText(getContext(), "empty fields error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void initObservers(){
        signupViewModel.getValidationError().observe(getViewLifecycleOwner(), errorCode -> {
            setErrorsOnViews();
            if (errorCode != null) {
                switch (errorCode) {
                    case 1: binding.nameTxt.setError("Name required"); break;
                    case 2: binding.emailTxt.setError("Invalid email"); break;
                    case 3: binding.passwordTxt.setError("Password too short"); break;
                    case 4: binding.confirmPasswordTxt.setError("Passwords do not match"); break;
                }
            }
        });

        signupViewModel.getRegistrationStatus().observe(getViewLifecycleOwner(), isDone -> {
            binding.linearlayout2.setVisibility(GONE);
            if (isDone){
                startActivity(new Intent(getContext(), MainActivity.class));
                Toast.makeText(getContext(), "Signed inSuccessfully...", Toast.LENGTH_SHORT).show();
                this.getActivity().finish();
            }else {
                Toast.makeText(getContext(), "User already exists...", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Glide.with(getContext()).load(uri).into(binding.userProfile);
                viewModel.uploadToCloudinary(uri,onCallback -> {
                    if (onCallback != null)
                        userImage = onCallback;
                });
            }

        }else if (requestCode == RC_SIGN_IN) {
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