package com.example.exam.auth;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam.Activity.MainActivity;
import com.example.exam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    EditText editTextEmail;
    EditText editTextPassword;
    TextView textView;
    CircularProgressButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        findViewById(R.id.textViewSignup).setOnClickListener(this);
        loginButton= findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(this);
        // For Password reset
        textView = findViewById(R.id.textViewReset);
        textView.setOnClickListener(view -> sendPasswordReset());
        editTextEmail.setTranslationX(1000f);
        editTextPassword.setTranslationX(1000f);
        loginButton.setTranslationX(1000f);
        viewAnimator();


    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        loginButton.startAnimation();
        loginButton.setFinalCornerRadius(200f);
        loginButton.setInitialCornerRadius(200f);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginButton.dispose();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    loginButton.revertAnimation(() -> {
                        loginButton.setFinalCornerRadius(200f);
                        loginButton.setInitialCornerRadius(200f);
                    });
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginButton.setTranslationX(1000f);
        editTextEmail.setTranslationX(1000f);
        editTextPassword.setTranslationX(1000f);
        viewAnimator();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginButton.setTranslationX(1000f);
        editTextEmail.setTranslationX(1000f);
        editTextPassword.setTranslationX(1000f);
        viewAnimator();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignup:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
                break;

            case R.id.buttonLogin:
                userLogin();
                break;
        }
    }

    // Password Reset Method
    public void sendPasswordReset() {
        // [START send_password_reset]
        String email = editTextEmail.getText().toString().trim();

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



        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        // [END send_password_reset]
    }

    public void viewAnimator(){
        ObjectAnimator loginButtonAnim = ObjectAnimator.ofFloat(loginButton, "translationX",1000f,0f);
        loginButtonAnim.setDuration(500);
        loginButtonAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        loginButtonAnim.start();

        ObjectAnimator emailAnim = ObjectAnimator.ofFloat(editTextEmail, "translationX",1000f,0f);
        emailAnim.setDuration(300);
        emailAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        emailAnim.start();

        ObjectAnimator passwordAnim = ObjectAnimator.ofFloat(editTextPassword, "translationX",1000f,0f);
        passwordAnim.setDuration(400);
        passwordAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        passwordAnim.start();
    }

    //snackbar
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
