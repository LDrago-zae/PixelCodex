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
import java.util.HashMap;
import java.util.Map;

public class AdminAddFragment extends Fragment {
    private EditText adminEmail, adminPassword;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_add, container, false);

        firestore = FirebaseFirestore.getInstance();
        adminEmail = view.findViewById(R.id.adminEmail);
        adminPassword = view.findViewById(R.id.adminPassword);
        Button saveButton = view.findViewById(R.id.saveAdminButton);

        saveButton.setOnClickListener(v -> {
            String email = adminEmail.getText().toString().trim();
            String password = adminPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                Map<String, Object> admin = new HashMap<>();
                admin.put("email", email);
                admin.put("password", password); // Note: Passwords should be hashed in production
                admin.put("timestamp", System.currentTimeMillis());

                firestore.collection("admin_users")
                        .add(admin)
                        .addOnSuccessListener(documentReference -> {
                            adminEmail.setText("");
                            adminPassword.setText("");
                            Toast.makeText(getContext(), "Admin added successfully", Toast.LENGTH_SHORT).show();// Return to dashboard
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to add admin", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}