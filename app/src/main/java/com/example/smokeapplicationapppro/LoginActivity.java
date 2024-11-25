package com.example.smokeapplicationapppro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {


    // UI components
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword; // Add TextView for Forgot Password


    // Firebase Authentication instance
    private FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Make sure this points to your login.xml layout


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        // Initialize UI components
        etEmail = findViewById(R.id.etEmail); // Email input field
        etPassword = findViewById(R.id.etPassword); // Password input field
        btnLogin = findViewById(R.id.btnLogin); // Login button
        tvRegister = findViewById(R.id.tvRegister); // Reference to the Register TextView
        tvForgotPassword = findViewById(R.id.tvForgotPassword); // Reference to the Forgot Password TextView


        // Set up login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();


                // Validate inputs
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Attempt to log in with Firebase Authentication
                    loginWithFirebase(email, password);
                }
            }
        });


        // Set up register TextView click listener
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // Assuming you have RegisterActivity
                startActivity(intent);
            }
        });


        // Set up forgot password click listener
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered email
                String email = etEmail.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    // Send password reset email
                    sendPasswordResetEmail(email);
                }
            }
        });
    }


    // Method to handle login with Firebase Authentication
    private void loginWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful, navigate to the MainActivity
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Navigate to MainActivity
                            startActivity(intent);
                            finish(); // Close the login activity to prevent going back to it
                        }
                    } else {
                        // Login failed, show an error message
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Method to send password reset email
    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Notify the user that an email has been sent
                        Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle error, such as invalid email
                        Toast.makeText(LoginActivity.this, "Error: Unable to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
