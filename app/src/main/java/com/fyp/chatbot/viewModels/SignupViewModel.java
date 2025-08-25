package com.fyp.chatbot.viewModels;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.R;
import com.fyp.chatbot.firebaseHelpers.SignupFB;
import com.fyp.chatbot.firebaseHelpers.UsersFB;
import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.interfaces.SignupInterface;
import com.fyp.chatbot.interfaces.UserInterFace;
import com.fyp.chatbot.models.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class SignupViewModel extends AndroidViewModel {
    private Context context;
    private final SignupInterface signupInterface = new SignupFB();
    private final UserInterFace userInterFace = new UsersFB();
    private final MutableLiveData<Boolean> registrationStatus = new MutableLiveData<>();
    MutableLiveData<Boolean> loginStatus = new MutableLiveData<>(false);
    MutableLiveData<String> username = new MutableLiveData<>();
    private MutableLiveData<Boolean> googleSignInStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> validationError = new MutableLiveData<>();

    public SignupViewModel(@NonNull Application application){
        super(application);
        context = application.getApplicationContext();
    }

    public void setUsername(String name) {
        username.setValue(name);
    }

    public boolean validateInputs(String name, String email, String pass, String confirmPass) {
        if (name == null || name.isEmpty()) {
            validationError.setValue(1);
            return false;
        } else if (email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            validationError.setValue(2);
            return false;
        } else if (pass == null || pass.length() < 6) {
            validationError.setValue(3);
            return false;
        } else if (!pass.equals(confirmPass)) {
            validationError.setValue(4);
            return false;
        }
        validationError.setValue(0); // no error
        return true;
    }

    public LiveData<Boolean> forgetPassword(String email) {
        MutableLiveData<Boolean> resetStatus = new MutableLiveData<>();
        signupInterface.resetPassword(email, resetStatus::setValue);
        return resetStatus;
    }
    public LiveData<Boolean> getGoogleSignInStatus() {
        return googleSignInStatus;
    }

    public void signInWithGoogle(GoogleSignInAccount account, String idToken) {
        String profile = account.getPhotoUrl() != null ?
                account.getPhotoUrl().toString() : "";
        signupInterface.firebaseAuthWithGoogle(idToken, result -> {
            if (result){
                String userId = FirebaseAuth.getInstance().getUid();
                updateUser(userId,account.getDisplayName(),account.getEmail(),profile,onResult -> {
                });
                googleSignInStatus.postValue(result);

            }else {
                googleSignInStatus.postValue(false);
            }
        });
    }

    public MutableLiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }
    public LiveData<Boolean> getRegistrationStatus() { return registrationStatus; }

    public void loginUser(String email,String password,String userImage,String userName) {
        signupInterface.loginUser(email, password, onResult -> {
                if (onResult){
                    String userId = FirebaseAuth.getInstance().getUid();
                    updateUser(userId,userName,email,userImage,onResult1 -> {
                        loginStatus.postValue(true);
                    });
                }
        });
    }

    public void registerUser(String email, String password,String userName,String userImage) {

        signupInterface.signupUser(email, password, isSigned -> {
            if (isSigned) {
                String userId = FirebaseAuth.getInstance().getUid();

                updateUser(userId,userName,email,userImage,onDataSave -> {
                    if (onDataSave){
                        registrationStatus.postValue(true);
                    }else {
                        registrationStatus.postValue(true);
                    }
                });
            } else {
                registrationStatus.setValue(false);
            }
        });
    }


    public void updateUser(String userId,String username, String email, String profileUrl, FirebaseCallback<Boolean> onSave) {
        userInterFace.addUser(new UserModel(userId,username,email,profileUrl),userId,
                onSave::onComplete);
    }
    public void updateImage(String userImage,FirebaseCallback<Boolean> onDataSave){
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null){
            userInterFace.setProfilePic(userId,userImage,onImageUpload -> {
                onDataSave.onComplete(onImageUpload);
            });
        }
    }
    public LiveData<Integer> getValidationError() { return validationError; }
}
