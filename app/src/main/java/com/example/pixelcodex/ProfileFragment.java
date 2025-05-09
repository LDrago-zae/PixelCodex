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
import androidx.appcompat.app.AlertDialog;
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
import java.util.Objects;

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

        // Initialize RecyclerView for options
        RecyclerView optionsRecyclerView = view.findViewById(R.id.optionsRecyclerView);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load options data
        List<ProfileOption> options = new ArrayList<>();
        options.add(new ProfileOption(R.drawable.notification, "Notifications"));
        options.add(new ProfileOption(R.drawable.bookmark2, "Manage Wishlist"));
        options.add(new ProfileOption(R.drawable.logout2, "Log Out"));

        // Set adapter with click listener
        ProfileOptionAdapter adapter = new ProfileOptionAdapter(getContext(), options, position -> {
            switch (position) {
                case 0: // Notifications
                    Toast.makeText(getContext(), "Notifications clicked", Toast.LENGTH_SHORT).show();
                    navigateToNotificationsFragment();
                    break;
                case 1: // Manage Wishlist
                    Toast.makeText(getContext(), "Manage Wishlist clicked", Toast.LENGTH_SHORT).show();
                    navigateToWishlistFragment();
                    break;
                case 2: // Log Out
                    alertDialog();
                    break;
            }
        });
        optionsRecyclerView.setAdapter(adapter);

        // Fetch and display user data
        fetchUserData();

        return view;
    }

    private void fetchUserData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String name = document.getString("name");
                                String profilePicture = document.getString("profilePicture");
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
            String[] sessionData = dbHelper.getSession();
            String discordAccessToken = sessionData[0];
            String discordUserId = sessionData[1];

            if (discordAccessToken != null && discordUserId != null) {
                Toast.makeText(getContext(), "Please sign in with Google or Email to view your profile", Toast.LENGTH_LONG).show();
                userName.setText("Discord User");
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
    }

    private void navigateToNotificationsFragment() {
        NotificationsFragment notificationsFragment = new NotificationsFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, notificationsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToWishlistFragment() {
        WishlistFragment wishlistFragment = new WishlistFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, wishlistFragment)
                .addToBackStack(null)
                .commit();
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Log Out")
                .setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            firebaseAuth.signOut();
            Toast.makeText(requireContext(), "You have logged out.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.alert_dialogue_rounded_background);
        dialog.show();
    }
}