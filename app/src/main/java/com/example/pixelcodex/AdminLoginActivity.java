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

public class AdminLoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private LottieAnimationView loaderAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login_activity);
        Log.d("AdminLoginActivity", "Layout set: " + (findViewById(R.id.signInButton) != null));

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

                // Simulate login process
                simulateLogin(email, password);
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

    private void simulateLogin(String email, String password) {
        // Simulate a delay for login (replace with actual authentication, e.g., Firebase)
        new android.os.Handler().postDelayed(() -> {
            // Hide loading indicators
            if (loaderAnimation != null) {
                loaderAnimation.setVisibility(View.GONE);
                loaderAnimation.cancelAnimation();
            }

            // Simple hardcoded admin check (replace with actual admin credentials check)
            if (email.equals("admin@example.com") && password.equals("admin123")) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                // Navigate to DashboardActivity
                startActivity(new Intent(AdminLoginActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }, 2000); // 2-second delay for simulation
    }
}