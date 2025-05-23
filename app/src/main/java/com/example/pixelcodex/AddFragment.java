package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class AddFragment extends Fragment {

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("game_requests");

        // Find the Lottie animation and text views
        LottieAnimationView addAnimation = view.findViewById(R.id.add_animation);
        TextView addMessage = view.findViewById(R.id.add_message);
        TextView addSubmessage = view.findViewById(R.id.add_submessage);

        // Apply fade-in animation
        addAnimation.setAlpha(0f);
        addMessage.setAlpha(0f);
        addSubmessage.setAlpha(0f);

        addAnimation.animate().alpha(1f).setDuration(500).start();
        addMessage.animate().alpha(1f).setDuration(500).setStartDelay(200).start();
        addSubmessage.animate().alpha(1f).setDuration(500).setStartDelay(400).start();

        // Show the bottom sheet
        showGameRequestBottomSheet();

        return view;
    }

    public void showGameRequestBottomSheet() {
        // Create the bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_game_request_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Find views in the bottom sheet
        TextInputEditText editGameTitle = bottomSheetView.findViewById(R.id.edit_game_title);
        TextInputEditText editGameDescription = bottomSheetView.findViewById(R.id.edit_game_description);
        AutoCompleteTextView editGamePlatform = bottomSheetView.findViewById(R.id.edit_game_platform);
        Button btnSubmitRequest = bottomSheetView.findViewById(R.id.btn_submit_request);

        // Set up the platform dropdown
        String[] platforms = {"PS5", "Xbox Series X|S", "PC", "Nintendo Switch", "PS4", "Xbox One"};
        ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                platforms
        );
        editGamePlatform.setAdapter(platformAdapter);

        // Handle form submission
        btnSubmitRequest.setOnClickListener(v -> {
            String gameTitle = editGameTitle.getText() != null ? editGameTitle.getText().toString().trim() : "";
            String gameDescription = editGameDescription.getText() != null ? editGameDescription.getText().toString().trim() : "";
            String gamePlatform = editGamePlatform.getText() != null ? editGamePlatform.getText().toString().trim() : "";

            // Basic validation
            if (gameTitle.isEmpty()) {
                editGameTitle.setError("Game title is required");
                return;
            }
            if (gameDescription.isEmpty()) {
                editGameDescription.setError("Description is required");
                return;
            }
            if (gamePlatform.isEmpty()) {
                editGamePlatform.setError("Platform is required");
                return;
            }

            // Create a HashMap to store the game request data
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("title", gameTitle);
            requestData.put("description", gameDescription);
            requestData.put("platform", gamePlatform);
            requestData.put("timestamp", System.currentTimeMillis());

            // Write to Firebase Realtime Database
            String requestId = databaseReference.push().getKey(); // Generate a unique ID for the request
            if (requestId != null) {
                databaseReference.child(requestId).setValue(requestData)
                        .addOnSuccessListener(aVoid -> {
                            // Show success message
                            String message = "Game Request Submitted:\n" +
                                    "Title: " + gameTitle + "\n" +
                                    "Description: " + gameDescription + "\n" +
                                    "Platform: " + gamePlatform;
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                            // Dismiss the bottom sheet
                            bottomSheetDialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            // Show error message
                            Toast.makeText(getContext(), "Failed to submit request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(getContext(), "Failed to generate request ID", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the bottom sheet
        bottomSheetDialog.show();
    }
}