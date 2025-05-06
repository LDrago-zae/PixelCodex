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

public class SupportFragment extends Fragment {

    private EditText subjectEditText;
    private EditText descriptionEditText;
    private EditText emailEditText;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize form fields
        subjectEditText = view.findViewById(R.id.query_subject);
        descriptionEditText = view.findViewById(R.id.query_description);
        emailEditText = view.findViewById(R.id.query_email);
        Button submitButton = view.findViewById(R.id.btn_submit_request);

        // Handle submit button click
        submitButton.setOnClickListener(v -> submitSupportQuery());

        return view;
    }

    private void submitSupportQuery() {
        // Collect form data
        String subject = subjectEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // Validate inputs
        if (subject.isEmpty() || description.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a SupportQuery object
        SupportQuery query = new SupportQuery(subject, description, email);

        // Save to Firestore
        db.collection("support_queries")
                .add(query)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Query submitted successfully", Toast.LENGTH_SHORT).show();
                    // Clear form fields
                    subjectEditText.setText("");
                    descriptionEditText.setText("");
                    emailEditText.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to submit query: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}