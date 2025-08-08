package com.fyp.chatbot.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
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

import android.util.Log;
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
    private String userName = "",userEmail="",userProfile = "";

    private SharedPreferences pref ;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileScreenBinding.inflate(inflater, container, false);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        pref = getActivity().getSharedPreferences("Smart_Goval",MODE_PRIVATE);
        editor = pref.edit();

        initListeners();
        getPreferences();
        return binding.getRoot();
    }

    private void getPreferences() {
        userName = pref.getString("UserName",null);
        userProfile = pref.getString("UserProfile",null);
        userEmail = pref.getString("UserEmail",null);


        if (userName != null){
            binding.userName.setText(userName);
        }
        if (userProfile != null)
            Glide.with(this.getContext()).load(userProfile)
                    .placeholder(R.drawable.account_circle_24px)
                    .error(R.drawable.account_circle_24px)
                    .into(binding.userProfile);

        if (userEmail != null){
            binding.userEmail.setText(userEmail);
        }

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
                uploadToCloudinary(uri);
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
                            Toast.makeText(getContext(), "uploaded", Toast.LENGTH_SHORT).show();
                            String userImage = resultData.get("secure_url").toString();
                            binding.linearlayout2.setVisibility(GONE);
                            userViewModel.updateImage(userImage,onResult -> {
                                if (onResult != null){
                                    Glide.with(getContext()).load(userImage)
                                            .placeholder(R.drawable.account_circle_24px)
                                            .error(R.drawable.account_circle_24px)
                                            .into(binding.userProfile);
                                    editor.putString("UserProfile",userImage);
                                    editor.apply();
                                    userViewModel.setProfileUpdated(true);
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