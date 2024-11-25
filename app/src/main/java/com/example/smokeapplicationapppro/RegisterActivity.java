package com.example.smokeapplicationapppro;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
    // UI elements using IDs from our layout
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton registerButton;
    private TextView loginLink;


    // Firebase instances
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private static final String TAG = "RegisterActivity"; // For logging


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        // Initialize views using IDs from our layout
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        usernameInput = findViewById(R.id.nameUnderline);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);


        // Set up button click listeners
        setupClickListeners();
    }


    private void setupClickListeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });


        // Set the login link click listener to redirect to LoginActivity
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the register screen to prevent back navigation to it
            }
        });
    }


    private void startRegistration() {
        // Get input values
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();


        // Validate inputs
        if (!validateInputs(name, email, username, password)) {
            return;
        }


        // Disable register button during registration process
        registerButton.setEnabled(false);


        // Start Firebase Authentication process
        createFirebaseUser(name, email, username, password);
    }


    private boolean validateInputs(String name, String email, String username, String password) {
        boolean isValid = true;


        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            isValid = false;
        }


        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            isValid = false;
        }


        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Username is required");
            isValid = false;
        }


        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            isValid = false;
        }


        return isValid;
    }


    private void createFirebaseUser(final String name, final String email,
                                    final String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the newly created user's ID
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Save additional user data to Realtime Database
                                saveUserToDatabase(firebaseUser.getUid(), name, email, username);
                            }
                        } else {
                            // Enable register button if registration fails
                            registerButton.setEnabled(true);
                            // Show error message
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Registration failed";
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Registration failed: " + errorMessage);
                        }
                    }
                });
    }


    private void saveUserToDatabase(String userId, String name, String email, String username) {
        // Create user data HashMap
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);  // Store name
        userData.put("email", email);  // Store email
        userData.put("username", username);  // Store username


        // Save to Firebase Database
        mDatabase.child("users").child(userId)
                .setValue(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable register button
                        registerButton.setEnabled(true);


                        if (task.isSuccessful()) {
                            // Show success message
                            Toast.makeText(RegisterActivity.this,
                                    "Registration successful!",
                                    Toast.LENGTH_SHORT).show();
                            // Close registration screen
                            finish();
                        } else {
                            // Show error message
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Failed to save user data";
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Database error: " + errorMessage);
                        }
                    }
                });
    }
}
