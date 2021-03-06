package com.example.exam.auth;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam.Activity.MainActivity;
import com.example.exam.Models.UserProfile;
import com.example.exam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private CircularProgressButton signup;

    private FirebaseUser user;
    private String name;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);



        mAuth = FirebaseAuth.getInstance();

        signup=findViewById(R.id.buttonSignUp);
        signup.setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);

        editTextEmail.setTranslationX(1000f);
        editTextPassword.setTranslationX(1000f);
        editTextName.setTranslationX(1000f);
        signup.setTranslationX(1000f);
        viewAnimator();

        //database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
    }

    private void registerUser() {
        name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        signup.startAnimation();
        signup.setFinalCornerRadius(200f);
        signup.setInitialCornerRadius(200f);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    signup.dispose();
                    //Adding user name
                    user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    user.updateProfile(profileUpdates).addOnSuccessListener(aVoid -> {
                        //User Created

                        //uploading user details
                        uploadUserDetails(user);
                    });




                } else {
                    signup.revertAnimation(() -> {
                        signup.setFinalCornerRadius(200f);
                        signup.setInitialCornerRadius(200f);
                    });
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                break;

            case R.id.textViewLogin:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    public void viewAnimator(){
        ObjectAnimator loginButtonAnim = ObjectAnimator.ofFloat(signup, "translationX",1000f,0f);
        loginButtonAnim.setDuration(600);
        loginButtonAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        loginButtonAnim.start();

        ObjectAnimator emailAnim = ObjectAnimator.ofFloat(editTextEmail, "translationX",1000f,0f);
        emailAnim.setDuration(500);
        emailAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        emailAnim.start();

        ObjectAnimator passwordAnim = ObjectAnimator.ofFloat(editTextPassword, "translationX",1000f,0f);
        passwordAnim.setDuration(400);
        passwordAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        passwordAnim.start();

        ObjectAnimator nameAnim = ObjectAnimator.ofFloat(editTextName, "translationX",1000f,0f);
        nameAnim.setDuration(300);
        nameAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        nameAnim.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        editTextEmail.setTranslationX(1000f);
        editTextPassword.setTranslationX(1000f);
        editTextName.setTranslationX(1000f);
        signup.setTranslationX(1000f);
        viewAnimator();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextEmail.setTranslationX(1000f);
        editTextPassword.setTranslationX(1000f);
        editTextName.setTranslationX(1000f);
        signup.setTranslationX(1000f);
        viewAnimator();
    }

    public void uploadUserDetails(FirebaseUser firebaseUser){

        String name = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        String uid = firebaseUser.getUid();
        UserProfile userProfile = new UserProfile(name,uid,email);

        databaseReference.child("UserProfile").child(uid).setValue(userProfile)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "Welcome "+name, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    else
                        Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                });


    }

}

