package com.example.pixelcodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private SessionDatabaseHelper dbHelper;
    private ImageView profileImage;
    private TextView userName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase and SQLite helper
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dbHelper = new SessionDatabaseHelper(getContext());

        // Initialize views
        profileImage = view.findViewById(R.id.profileImage);
        userName = view.findViewById(R.id.userName);
        Button viewProfileButton = view.findViewById(R.id.view_profile_button);

        // Initialize buttons
        viewProfileButton.setOnClickListener(v -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            String[] sessionData = dbHelper.getSession();
            String discordAccessToken = sessionData[0];
            String discordUserId = sessionData[1];

            AccountFragment accountFragment = new AccountFragment();
            Bundle args = new Bundle();
            if (currentUser != null) {
                args.putString("uid", currentUser.getUid());
                args.putBoolean("isFirebaseUser", true);
            } else if (discordAccessToken != null && discordUserId != null) {
                args.putString("discordUserId", discordUserId);
                args.putBoolean("isFirebaseUser", false);
            }
            accountFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, accountFragment)
                    .addToBackStack(null)
                    .commit();
        });



        Button addFriendsButton = view.findViewById(R.id.add_friends_button);

        // Initialize RecyclerView for options
        RecyclerView optionsRecyclerView = view.findViewById(R.id.optionsRecyclerView);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load options data
        List<ProfileOption> options = new ArrayList<>();
        options.add(new ProfileOption(R.drawable.profile2, "Account Info"));
        options.add(new ProfileOption(R.drawable.edit1, "Edit Profile"));
        options.add(new ProfileOption(R.drawable.gift2, "Gifts"));
        options.add(new ProfileOption(R.drawable.bookmark2, "Manage Wishlist"));

        // Set adapter
        ProfileOptionAdapter adapter = new ProfileOptionAdapter(getContext(), options);
        optionsRecyclerView.setAdapter(adapter);

        // Fetch and display user data
        fetchUserData();

        // Set click listener for add friends button

        addFriendsButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add Friends clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void fetchUserData() {
        // First, check if the user is logged in via Firebase (Google or email/password)
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // Firebase user is logged in, fetch data from Firestore
            String uid = currentUser.getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String name = document.getString("name");
                                String profilePicture = document.getString("profilePicture");
                                // Update UI
                                userName.setText(name != null ? name : "Unknown User");

                                if (profilePicture != null && !profilePicture.isEmpty()) {
                                    Glide.with(this)
                                            .load(profilePicture)
                                            .placeholder(R.drawable.circular_profile)
                                            .into(profileImage);
                                }
                            } else {
                                Toast.makeText(getContext(), "User data not found in Firestore", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No Firebase user, check for Discord session in SQLite
            String[] sessionData = dbHelper.getSession();
            String discordAccessToken = sessionData[0];
            String discordUserId = sessionData[1];

            if (discordAccessToken != null && discordUserId != null) {
                // User is logged in via Discord, but Discord data isn't in Firestore
                Toast.makeText(getContext(), "Please sign in with Google or Email to view your profile", Toast.LENGTH_LONG).show();
                userName.setText("Discord User");
                // Optionally redirect to login screen to encourage Firebase login
                // startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                // No user logged in at all
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
    }
}