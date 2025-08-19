package com.fyp.chatbot.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.fyp.chatbot.R;
import com.fyp.chatbot.activities.IntroActivity;
import com.fyp.chatbot.databinding.FragmentProfileScreenBinding;
import com.fyp.chatbot.viewModels.SharedPreferenceViewModel;
import com.fyp.chatbot.viewModels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executors;

public class ProfileScreen extends Fragment {

    private final int REQUEST_CODE_IMAGE = 101;
    FragmentProfileScreenBinding binding;
    UserViewModel userViewModel;
    SharedPreferenceViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileScreenBinding.inflate(inflater, container, false);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        viewModel = new ViewModelProvider(this,new ViewModelProvider
                .AndroidViewModelFactory(this.getActivity().getApplication()))
                .get(SharedPreferenceViewModel.class);


        initListeners();
        getPreferences();
        return binding.getRoot();
    }

    private void getPreferences() {
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