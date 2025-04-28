package com.example.pixelcodex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import java.util.concurrent.Executors;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;
    private LottieAnimationView lottieProgress; // For Google Sign-In loading
    private ProgressBar progressBar; // For email/password sign-up loading

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Credential Manager
        credentialManager = CredentialManager.create(this);

        // Find Views
        emailEditText = findViewById(R.id.emailField);
        passwordEditText = findViewById(R.id.passwordField);
        Button signupButton = findViewById(R.id.signUpButton);
        Button signInButton = findViewById(R.id.loginButton);
        ImageButton googleSignInButton = findViewById(R.id.googleIcon);
        lottieProgress = findViewById(R.id.loaderAnimation);
        progressBar = findViewById(R.id.progressBar);
        LinearLayout rootLayout = findViewById(R.id.main);

        rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) clearFocusAndHideKeyboard();
            return false; // Allow other touch events to propagate
        });

        // Configure Google Sign-In request
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest googleSignInRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        // Google Sign-In Button Listener
        googleSignInButton.setOnClickListener(v -> {
            CancellationSignal cancellationSignal = new CancellationSignal();
            credentialManager.getCredentialAsync(
                    this,
                    googleSignInRequest,
                    cancellationSignal,
                    Executors.newSingleThreadExecutor(),
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(GetCredentialResponse response) {
                            runOnUiThread(() -> {
                                lottieProgress.setVisibility(View.VISIBLE);
                                lottieProgress.playAnimation();
                                try {
                                    Credential credential = response.getCredential();
                                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                                    firebaseAuthWithGoogle(googleIdTokenCredential);
                                } catch (Exception e) {
                                    lottieProgress.setVisibility(View.GONE);
                                    lottieProgress.cancelAnimation();
                                    Log.e(TAG, "Google Sign-In error", e);
                                    Toast.makeText(MainActivity2.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(@NonNull GetCredentialException e) {
                            runOnUiThread(() -> {
                                lottieProgress.setVisibility(View.GONE);
                                lottieProgress.cancelAnimation();
                                Log.e(TAG, "Google Sign-In error", e);
                                Toast.makeText(MainActivity2.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
            );
        });

        // Sign In Button Listener
        signInButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(v -> registerUser());
    }

    private void firebaseAuthWithGoogle(GoogleIdTokenCredential credential) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(credential.getIdToken(), null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task -> {
                    lottieProgress.setVisibility(View.GONE);
                    lottieProgress.cancelAnimation();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Google Sign-In Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearFocusAndHideKeyboard() {
        emailEditText.clearFocus();
        passwordEditText.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}