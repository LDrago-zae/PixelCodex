package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {

    private ImageView profileImage;
    private TextView userName, userIdText, userEmailText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SessionDatabaseHelper dbHelper = new SessionDatabaseHelper(requireContext());

        profileImage = view.findViewById(R.id.profileImage);
        userName = view.findViewById(R.id.userName);
        userIdText = view.findViewById(R.id.userIdText);
        userEmailText = view.findViewById(R.id.userEmailText);

        // Get arguments
        Bundle args = getArguments();
        if (args != null) {
            boolean isFirebaseUser = args.getBoolean("isFirebaseUser", true);
            if (isFirebaseUser) {
                String uid = args.getString("uid");
                if (uid != null) {
                    db.collection("users").document(uid)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        String name = document.getString("name");
                                        String profilePicture = document.getString("profilePicture");
                                        String email = document.getString("email"); // Assuming email is saved in Firestore

                                        userName.setText(name != null ? name : "Unknown User");
                                        userIdText.setText("User ID: " + uid);
                                        userEmailText.setText("Email: " + (email != null ? email : "Not available"));

                                        if (profilePicture != null && !profilePicture.isEmpty()) {
                                            Glide.with(this)
                                                    .load(profilePicture)
                                                    .placeholder(R.drawable.circular_profile)
                                                    .into(profileImage);
                                        }
                                    } else {
                                        Toast.makeText(requireContext(), "Account data not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Failed to fetch account data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                String discordUserId = args.getString("discordUserId");
                if (discordUserId != null) {
                    userName.setText("Discord User");
                    userIdText.setText("User ID: " + discordUserId);
                    userEmailText.setText("Email: Not available (Discord login)");
                    Toast.makeText(requireContext(), "Full Discord profile not available. Please use Google/Email login.", Toast.LENGTH_LONG).show();
                }
            }
        }

        return view;
    }
}