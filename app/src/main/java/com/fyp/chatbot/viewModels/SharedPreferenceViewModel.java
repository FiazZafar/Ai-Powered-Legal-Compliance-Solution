package com.fyp.chatbot.viewModels;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.fyp.chatbot.firebaseHelpers.UsersFB;
import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.interfaces.UserInterFace;
import com.fyp.chatbot.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class SharedPreferenceViewModel extends AndroidViewModel {
    UserInterFace userInterFace = new UsersFB();
    private SharedPreferences pref ;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferenceViewModel(@NonNull Application application){
        super(application);
        pref = application.getApplicationContext().getSharedPreferences("Smart_Goval", Context.MODE_PRIVATE);
        editor = pref.edit();

        context = application.getApplicationContext();
    }
    private MutableLiveData<UserModel> data = new MutableLiveData<>();

    public MutableLiveData<UserModel> getData() {
        String userName = pref.getString("UserName","");
        String userEmail = pref.getString("UserEmail","");
        String userProfile = pref.getString("UserProfile","");
        data.postValue(new UserModel("",userName,userProfile,userEmail));
        return data;
    }
    public void savedData(String userName,String userEmail,String userProfile){
        if (userName.isEmpty()){
            editor.putString("UserName", userName);
            editor.apply();
        }
        if (userEmail.isEmpty()){
            editor.putString("UserEmail", userEmail);
            editor.apply();
        }
        if (userProfile.isEmpty()){
            editor.putString("UserProfile",userProfile);
            editor.apply();
        }
    }


    public void uploadToCloudinary(Uri uri,FirebaseCallback<String> oncallback) {
            try {
                File compressedFile = compressToMax200Kb(context,uri);

                    MediaManager.get().upload(Uri.fromFile(compressedFile))
                            .callback(new UploadCallback() {
                                @Override
                                public void onStart(String requestId) {}
                                @Override
                                public void onProgress(String requestId, long bytes, long totalBytes) {}
                                @Override
                                public void onSuccess(String requestId, Map resultData) {
                                    String userImage = resultData.get("secure_url").toString();
                                    updateImage(userImage, onResult -> {
                                        if (onResult){
                                            editor.putString("UserProfile",userImage);
                                            editor.apply();

                                            oncallback.onComplete(userImage);
                                        }
                                    });
                                }

                                @Override
                                public void onError(String requestId, ErrorInfo error) {

                                }

                                @Override
                                public void onReschedule(String requestId, ErrorInfo error) {

                                }
                });
            } catch (IOException e) {
                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
    }

    public void updateImage(String userImage, FirebaseCallback<Boolean> onDataSave){
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null){
            userInterFace.setProfilePic(userId,userImage,onImageUpload -> {
                onDataSave.onComplete(onImageUpload);
            });
        }
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
