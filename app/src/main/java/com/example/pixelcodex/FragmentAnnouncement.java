package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentAnnouncement extends Fragment {
    private EditText descriptionEditText;
    private EditText emailEditText;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        // Initialize UI elements
        descriptionEditText = view.findViewById(R.id.announcementDescription);
        emailEditText = view.findViewById(R.id.announcementEmail);
        Button sendButton = view.findViewById(R.id.sendButton);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set click listener for Send button
        sendButton.setOnClickListener(v -> {
            String description = descriptionEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            if (!description.isEmpty() && !email.isEmpty()) {
                // Create AnnouncementItem object
                AnnouncementItem announcement = new AnnouncementItem(description, email);

                // Save to Firestore
                firestore.collection("announcements")
                        .add(announcement)
                        .addOnSuccessListener(documentReference -> {
                            // Clear fields on success
                            descriptionEditText.setText("");
                            emailEditText.setText("");
                            Toast.makeText(getContext(), "Announcement sent!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure (e.g., show error to user)
                        });
            }
        });

        return view;
    }
}