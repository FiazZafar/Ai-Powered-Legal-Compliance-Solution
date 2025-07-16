package com.fyp.chatbot.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.chatbot.MainActivity;
import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    SharedPreferences preferences ;
    SharedPreferences.Editor editor ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("UsersProfilePref",MODE_PRIVATE);
        editor = preferences.edit();


        binding.loginBtn.setOnClickListener(v -> getCredentials());
        binding.signUpBtn.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
        binding.googleBtn.setOnClickListener(v -> signUpWithGoogle());

        binding.eyeVisibleconfirmBTn.setOnClickListener(view -> {
            if (binding.passwordTxt.getInputType() == (InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                if (!binding.passwordTxt.getText().toString().equals("")){
                    binding.passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT
                            |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.eyeVisibleconfirmBTn.setImageResource(R.drawable.visibility_off_24px);
                }
            }else {
                if (!binding.passwordTxt.getText().toString().equals("")){
                    binding.passwordTxt.setInputType(InputType.TYPE_CLASS_TEXT|
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.eyeVisibleconfirmBTn.setImageResource(R.drawable.visibility_24px);
                }
            }
        });

    }

    private void signUpWithGoogle() {

    }

    private void  getCredentials(){
        String userName = binding.userName.getText().toString();
        String emailTxt = binding.emailTxt.getText().toString();
        String passwordTxt = binding.passwordTxt.getText().toString();

        if (emailTxt == null || passwordTxt == null ){
            binding.loginBtn.setError("fill all credentials...");
        }else {
            setupLogin(emailTxt,passwordTxt,userName);
        }
    }
    private void setupLogin(String emailTxt ,String passwordTxt,String userName){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(this, "Login Successfull...", Toast.LENGTH_SHORT).show();
                editor.putString("userName",userName);
                editor.putString("userEmail",emailTxt);
                editor.apply();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

}