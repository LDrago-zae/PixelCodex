package com.example.pixelcodex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CredentialManager credentialManager;
    private ProgressBar progressBar;
    private LottieAnimationView lottieProgress;
    private OkHttpClient client;
    private SessionDatabaseHelper dbHelper;
    private String discordAccessToken;
    private String discordUserId;
    private Button signInButton, signUpButton, signInAsAdminButton;
    private ImageButton googleSignInButton, discordSignInButton;
    private LinearLayout rootLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Credential Manager
        credentialManager = CredentialManager.create(this);

        // Initialize OkHttpClient
        client = new OkHttpClient();

        // Initialize SQLite Database Helper
        dbHelper = new SessionDatabaseHelper(this);

        // Find views
        emailEditText = findViewById(R.id.emailField);
        passwordEditText = findViewById(R.id.passwordField);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpText);
        signInAsAdminButton = findViewById(R.id.signInAsAdminButton);
        googleSignInButton = findViewById(R.id.googleIcon);
        discordSignInButton = findViewById(R.id.steamIcon);
        progressBar = findViewById(R.id.progressBar);
        lottieProgress = findViewById(R.id.loaderAnimation);
        rootLayout = findViewById(R.id.main);

        rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) clearFocusAndHideKeyboard();
            return false;
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
        if (googleSignInButton != null) {
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
                                    if (lottieProgress != null) {
                                        lottieProgress.setVisibility(View.VISIBLE);
                                        lottieProgress.playAnimation();
                                    }
                                    try {
                                        Credential credential = response.getCredential();
                                        GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                                        firebaseAuthWithGoogle(googleIdTokenCredential);
                                    } catch (Exception e) {
                                        if (lottieProgress != null) {
                                            lottieProgress.setVisibility(View.GONE);
                                            lottieProgress.cancelAnimation();
                                        }
                                        Log.e(TAG, "Google Sign-In error", e);
                                        Toast.makeText(MainActivity.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onError(@NonNull GetCredentialException e) {
                                runOnUiThread(() -> {
                                    if (lottieProgress != null) {
                                        lottieProgress.setVisibility(View.GONE);
                                        lottieProgress.cancelAnimation();
                                    }
                                    Log.e(TAG, "Google Sign-In error", e);
                                    Toast.makeText(MainActivity.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                );
            });
        }

        // Discord Sign-In Button Listener
        if (discordSignInButton != null) {
            discordSignInButton.setOnClickListener(v -> {
                String discordAuthUrl = "https://discord.com/api/oauth2/authorize" +
                        "?client_id=" + getString(R.string.discord_client_id) +
                        "&redirect_uri=" + Uri.encode("pixelcodex://callback") +
                        "&response_type=code" +
                        "&scope=identify%20email";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(discordAuthUrl));
                startActivity(intent);
            });
        }

        // Sign In Button Listener (Email/Password)
        if (signInButton != null) {
            signInButton.setOnClickListener(v -> loginUser());
        }

        // Sign Up Button Listener
        if (signUpButton != null) {
            signUpButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            });
        }

        // Sign In as Admin Button Listener
        if (signInAsAdminButton != null) {
            signInAsAdminButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in (Firebase Auth for Google/Email or Discord session in SQLite)
        if (mAuth.getCurrentUser() != null) {
            // User is signed in with Firebase (Google or Email/Password)
            Intent intent = new Intent(MainActivity.this, MainActivity3.class);
            startActivity(intent);
            finish();
        } else {
            // Check for Discord session in SQLite
            checkDiscordSession();
        }
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDiscordCallback();
    }

    private void checkDiscordSession() {
        String[] sessionData = dbHelper.getSession();
        discordAccessToken = sessionData[0];
        discordUserId = sessionData[1];

        if (discordAccessToken != null && discordUserId != null) {
            // User has a valid Discord session
            Toast.makeText(MainActivity.this, "Signed in with Discord!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity3.class);
            startActivity(intent);
            finish();
        }
    }

    private void handleDiscordCallback() {
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith("pixelcodex://callback")) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                if (lottieProgress != null) {
                    lottieProgress.setVisibility(View.VISIBLE);
                    lottieProgress.playAnimation();
                }
                new Thread(() -> {
                    try {
                        // Exchange the code for an access token
                        RequestBody formBody = new FormBody.Builder()
                                .add("client_id", getString(R.string.discord_client_id))
                                .add("client_secret", getString(R.string.discord_client_secret))
                                .add("grant_type", "authorization_code")
                                .add("code", code)
                                .add("redirect_uri", "pixelcodex://callback")
                                .build();

                        Request request = new Request.Builder()
                                .url("https://discord.com/api/oauth2/token")
                                .post(formBody)
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .build();

                        try (Response response = client.newCall(request).execute()) {
                            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                            String responseBody = response.body().string();
                            JSONObject json = new JSONObject(responseBody);
                            discordAccessToken = json.getString("access_token");

                            // Get the Discord user ID
                            Request userRequest = new Request.Builder()
                                    .url("https://discord.com/api/users/@me")
                                    .header("Authorization", "Bearer " + discordAccessToken)
                                    .build();

                            try (Response userResponse = client.newCall(userRequest).execute()) {
                                if (!userResponse.isSuccessful()) throw new IOException("Unexpected code " + userResponse);
                                String userResponseBody = userResponse.body().string();
                                JSONObject userJson = new JSONObject(userResponseBody);
                                discordUserId = userJson.getString("id");
                                String discordName = userJson.getString("username");
                                String discordAvatar = userJson.has("avatar") ? "https://cdn.discordapp.com/avatars/" + discordUserId + "/" + userJson.getString("avatar") + ".png" : "";

                                // Store the Discord session in SQLite
                                dbHelper.saveSession(discordAccessToken, discordUserId);

                                runOnUiThread(() -> {
                                    if (lottieProgress != null) {
                                        lottieProgress.setVisibility(View.GONE);
                                        lottieProgress.cancelAnimation();
                                    }
                                    Toast.makeText(this, "Discord Sign-In Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                                    startActivity(intent);
                                    finish();
                                });
                            }
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            if (lottieProgress != null) {
                                lottieProgress.setVisibility(View.GONE);
                                lottieProgress.cancelAnimation();
                            }
                            Toast.makeText(this, "Discord Sign-In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            } else {
                Toast.makeText(this, "Discord Sign-In failed: No code received", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleIdTokenCredential credential) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(credential.getIdToken(), null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task -> {
                    if (lottieProgress != null) {
                        lottieProgress.setVisibility(View.GONE);
                        lottieProgress.cancelAnimation();
                    }
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String name = user.getDisplayName() != null ? user.getDisplayName() : "Unknown User";
                            String profilePicture = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
                            saveUserDataToFirestore(name, profilePicture);
                        }
                        Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Google Sign-In Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loginUser() {
        String email = emailEditText != null ? emailEditText.getText().toString().trim() : "";
        String password = passwordEditText != null ? passwordEditText.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            if (emailEditText != null) {
                emailEditText.setError("Email is required");
                emailEditText.requestFocus();
            }
            return;
        }

        if (TextUtils.isEmpty(password)) {
            if (passwordEditText != null) {
                passwordEditText.setError("Password is required");
                passwordEditText.requestFocus();
            }
            return;
        }

        if (password.length() < 6) {
            if (passwordEditText != null) {
                passwordEditText.setError("Password must be at least 6 characters");
                passwordEditText.requestFocus();
            }
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Use email as name if display name is not available
                            String name = user.getDisplayName() != null ? user.getDisplayName() : email;
                            String profilePicture = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
                            saveUserDataToFirestore(name, profilePicture);
                        }
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserDataToFirestore(String name, String profilePicture) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("profilePicture", profilePicture);
            userData.put("email", user.getEmail());
            db.collection("users").document(user.getUid())
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User data saved to Firestore"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error saving user data", e));
        }
    }

    private void clearFocusAndHideKeyboard() {
        if (emailEditText != null) emailEditText.clearFocus();
        if (passwordEditText != null) passwordEditText.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}