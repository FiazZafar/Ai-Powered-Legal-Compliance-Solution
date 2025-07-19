package com.fyp.chatbot.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.fyp.chatbot.firebaseHelpers.SignupFB;
import com.fyp.chatbot.firebaseHelpers.UsersFB;
import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.interfaces.SignupInterface;
import com.fyp.chatbot.interfaces.UserInterFace;
import com.fyp.chatbot.models.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class SignupMVVM extends ViewModel {
    private final SignupInterface signupInterface = new SignupFB();
    private final UserInterFace userInterFace = new UsersFB();
    private final MutableLiveData<Boolean> registrationStatus = new MutableLiveData<>();
    MutableLiveData<Boolean> loginStatus = new MutableLiveData<>(false);
    public LiveData<Boolean> forgetPassword(String email) {
        MutableLiveData<Boolean> resetStatus = new MutableLiveData<>();
        signupInterface.resetPassword(email, resetStatus::setValue);
        return resetStatus;
    }
    private MutableLiveData<Boolean> googleSignInStatus = new MutableLiveData<>();
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

            }
        });
    }

    public MutableLiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }

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

    public LiveData<Boolean> getRegistrationStatus() { return registrationStatus; }
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
}
