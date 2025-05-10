package com.example.pixelcodex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot; // Explicit import

public class AdminLoginActivity extends AppCompatActivity {

    private static final String TAG = "AdminLoginActivity";
    private EditText emailField, passwordField;
    private LottieAnimationView loaderAnimation;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login_activity);
        Log.d(TAG, "Layout set: " + (findViewById(R.id.signInButton) != null));

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        Button signInButton = findViewById(R.id.signInButton);
        Button backToUserButton = findViewById(R.id.BackToUserButton);
        loaderAnimation = findViewById(R.id.loaderAnimation);

        // Sign-in button click listener with null check
        if (signInButton != null) {
            signInButton.setOnClickListener(v -> {
                String email = emailField != null ? emailField.getText().toString().trim() : "";
                String password = passwordField != null ? passwordField.getText().toString().trim() : "";

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show loading indicators
                if (loaderAnimation != null) {
                    loaderAnimation.setVisibility(View.VISIBLE);
                    loaderAnimation.playAnimation();
                }

                // Check credentials in Firestore
                checkAdminCredentials(email, password);
            });
        } else {
            Toast.makeText(this, "Sign-in button not found", Toast.LENGTH_SHORT).show();
        }

        // Back to user login button click listener
        backToUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkAdminCredentials(String email, String password) {
        firestore.collection("admin_users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (loaderAnimation != null) {
                        loaderAnimation.setVisibility(View.GONE);
                        loaderAnimation.cancelAnimation();
                    }

                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) { // Corrected loop
                            String storedPassword = document.getString("password");
                            if (storedPassword != null && storedPassword.equals(password)) {
                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdminLoginActivity.this, DashboardActivity.class));
                                finish();
                                return;
                            }
                        }
                        Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No admin found with this email", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (loaderAnimation != null) {
                        loaderAnimation.setVisibility(View.GONE);
                        loaderAnimation.cancelAnimation();
                    }
                    Log.e(TAG, "Firestore query failed: " + e.getMessage());
                    Toast.makeText(this, "Error checking credentials: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}