package com.fyp.chatbot.fragments;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.fyp.chatbot.activities.MainActivity;
import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.FragmentLoginBinding;
import com.fyp.chatbot.viewModels.SignupViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private String userEmail,userPassword,userName,userImage;
    SignupViewModel signupViewModel;
    private final int REQUEST_CODE_IMAGE = 101;
    private static final int RC_SIGN_IN = 1001;

    public LoginFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        visibilityListener();
        authListeners();
        initObservers();
        generalListeners();
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
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });

        });

    }
    private void generalListeners() {

        binding.uploadProfile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_IMAGE);
        });
    }
    private void initObservers() {
        signupViewModel.getLoginStatus().observe(getViewLifecycleOwner(), onLogin->{
            binding.linearlayout2.setVisibility(GONE);
            if (onLogin){
                Toast.makeText(this.getActivity(), "Login Successful...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this.getActivity(), MainActivity.class));
                this.getActivity().finish();
            }else{
                Toast.makeText(this.getActivity(), "Login Failed...", Toast.LENGTH_SHORT).show();
            }
        });


        signupViewModel.getGoogleSignInStatus().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {

                Toast.makeText(getContext(), "Signed in with Google successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), MainActivity.class));

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

            Boolean isValid = validateInputs();
            setErrorsOnViews();

            if (isValid) {
                binding.linearlayout2.setVisibility(View.VISIBLE);
                signupViewModel.loginUser(userEmail,userPassword,userImage,userName);
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
    private Boolean validateInputs(){

        // Email validation
        if (userEmail.isEmpty()) {
            binding.emailTxt.setError("Email is required");
            binding.emailTxt.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            binding.emailTxt.setError("Please enter a valid email");
            binding.emailTxt.requestFocus();
            return false;
        }

        // Password validation
        if (userName.isEmpty()) {
            binding.userName.setError("UserName is required");
            binding.userName.requestFocus();
            return false;
        } else if (userName.length() < 8) {
            binding.userName.setError("UserName is required");
            binding.userName.requestFocus();
            return false;
        }

        // Password validation
        if (userPassword.isEmpty()) {
            binding.passwordTxt.setError("Username is required");
            binding.passwordTxt.requestFocus();
            return false;
        } else if (userPassword == null) {
            binding.passwordTxt.setError("Username is required");
            binding.passwordTxt.requestFocus();
            return false;
        }
        return true;
    }
    private void setErrorsOnViews() {
        binding.passwordTxt.setError(null);
        binding.passwordTxt.setError(null);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Glide.with(getContext()).load(uri).into(binding.userProfile);
                uploadToCloudinary(uri);
            }
        }else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                signupViewModel.signInWithGoogle(account,account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadToCloudinary(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                File compressedFile = compressToMax200Kb(requireContext(),uri);

                requireActivity().runOnUiThread(() ->{
                    MediaManager.get().upload(Uri.fromFile(compressedFile))
                            .callback(new UploadCallback() {
                                @Override
                                public void onStart(String requestId) {}
                                @Override
                                public void onProgress(String requestId, long bytes, long totalBytes) {}
                                @Override
                                public void onSuccess(String requestId, Map resultData) {
                                    userImage = resultData.get("secure_url").toString();
                                    signupViewModel.updateImage(userImage, onResult -> {
                                        if (onResult){
                                           binding.userProfile.setImageURI(uri);
                                        }
                                    });

                                }

                                @Override
                                public void onError(String requestId, ErrorInfo error) {

                                }

                                @Override
                                public void onReschedule(String requestId, ErrorInfo error) {

                                }
                            }).dispatch();
                });
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private File compressToMax200Kb(Context context, Uri uri) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateMaxSize(context,uri,1024);
        Bitmap bitmap = BitmapFactory.decodeStream(input,null,options);
        input.close();

        File output = File.createTempFile("compressed","jpg",context.getCacheDir());
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

        int quality = 90 ;

        do{
            arrayOutputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG,quality,arrayOutputStream);
            quality -= 10;
        }while (arrayOutputStream.size() > 200 * 1024 && quality > 30);

        try (FileOutputStream fos = new FileOutputStream(output)){
            fos.write(arrayOutputStream.toByteArray());
        }

        bitmap.recycle();
        return output;
    }
    private int calculateMaxSize(Context context, Uri uri, int maxDim) {
        int scale = 1;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream,null,options);
            inputStream.close();

            while (options.outWidth / scale > maxDim || options.outHeight / scale > maxDim){
                scale *= 2;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scale;

    }

}