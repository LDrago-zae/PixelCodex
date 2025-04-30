package com.example.pixelcodex;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GameRequestsFragment extends Fragment {

    private static final String TAG = "GameRequestsFragment";
    private DatabaseReference databaseReference;
    private GameRequestAdapter adapter;
    private final List<Map<String, Object>> gameRequests = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_requests, container, false);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("game_requests");

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.game_requests_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GameRequestAdapter(gameRequests);
        recyclerView.setAdapter(adapter);

        // Fetch game requests from Firebase
        fetchGameRequests();

        // Run the one-time update to fix existing entries (remove after running once)
        updateExistingGameRequests();

        return view;
    }

    private void fetchGameRequests() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameRequests.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> request = (Map<String, Object>) dataSnapshot.getValue();
                    if (request != null) {
                        String key = dataSnapshot.getKey();
                        // Log if requestId is missing for debugging
                        if (!request.containsKey("requestId")) {
                            Log.w(TAG, "requestId missing for key: " + key + ", using key as fallback");
                            request.put("requestId", key);
                        }
                        gameRequests.add(request);
                    }
                }
                adapter.notifyDataSetChanged();
                if (gameRequests.isEmpty()) {
                    Toast.makeText(getContext(), "No game requests found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load game requests: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateExistingGameRequests() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    Map<String, Object> request = (Map<String, Object>) dataSnapshot.getValue();
                    if (request != null && !request.containsKey("requestId")) {
                        Log.i(TAG, "Adding requestId to entry with key: " + key);
                        request.put("requestId", key);
                        assert key != null;
                        databaseReference.child(key).setValue(request)
                                .addOnSuccessListener(aVoid -> Log.i(TAG, "Successfully updated requestId for key: " + key))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to update requestId for key: " + key + ", error: " + e.getMessage()));
                    }
                }
                Toast.makeText(getContext(), "Updated existing game requests with requestId", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to update game requests: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showUpdateDialog(Map<String, Object> request) {
        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_game_request, null);
        builder.setView(dialogView);

        // Find views in the dialog
        TextInputEditText editGameTitle = dialogView.findViewById(R.id.edit_game_title);
        TextInputEditText editGameDescription = dialogView.findViewById(R.id.edit_game_description);
        AutoCompleteTextView editGamePlatform = dialogView.findViewById(R.id.edit_game_platform);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);

        // Pre-fill the form with existing data
        editGameTitle.setText((String) request.get("title"));
        editGameDescription.setText((String) request.get("description"));
        editGamePlatform.setText((String) request.get("platform"));

        // Set up the platform dropdown
        String[] platforms = {"PS5", "Xbox Series X|S", "PC", "Nintendo Switch", "PS4", "Xbox One"};
        ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                platforms
        );
        editGamePlatform.setAdapter(platformAdapter);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Handle Save button
        btnSave.setOnClickListener(v -> {
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

            // Create a HashMap to store the updated game request data
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("title", gameTitle);
            updatedData.put("description", gameDescription);
            updatedData.put("platform", gamePlatform);
            updatedData.put("timestamp", System.currentTimeMillis());
            updatedData.put("requestId", request.get("requestId"));

            // Update the request in Firebase
            String requestId = (String) request.get("requestId");
            if (requestId != null) {
                databaseReference.child(requestId).setValue(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Game request updated successfully", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update request: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } else {
                Toast.makeText(getContext(), "Failed to update: Request ID not found", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(Map<String, Object> request) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Request")
                .setMessage("Do you want to delete it permanently?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String requestId = (String) request.get("requestId");
                    if (requestId != null) {
                        databaseReference.child(requestId).removeValue()
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Game request deleted successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete request: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    } else {
                        Toast.makeText(getContext(), "Unable to delete: Request ID not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Adapter for RecyclerView
    private class GameRequestAdapter extends RecyclerView.Adapter<GameRequestAdapter.GameRequestViewHolder> {

        private final List<Map<String, Object>> gameRequests;

        public GameRequestAdapter(List<Map<String, Object>> gameRequests) {
            this.gameRequests = gameRequests;
        }

        @NonNull
        @Override
        public GameRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_requests, parent, false);
            return new GameRequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GameRequestViewHolder holder, int position) {
            Map<String, Object> request = gameRequests.get(position);
            holder.title.setText((String) request.get("title"));
            holder.description.setText((String) request.get("description"));
            holder.platform.setText((String) request.get("platform"));
            // Format the timestamp
            Long timestamp = (Long) request.get("timestamp");
            if (timestamp != null) {
                String formattedDate = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        .format(new Date(timestamp));
                holder.timestamp.setText(formattedDate);
            } else {
                holder.timestamp.setText("Unknown date");
            }

            // Handle Update button click
            holder.updateButton.setOnClickListener(v -> showUpdateDialog(request));

            // Handle Delete button click
            holder.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(request));
        }

        @Override
        public int getItemCount() {
            return gameRequests.size();
        }

        class GameRequestViewHolder extends RecyclerView.ViewHolder {
            TextView title, description, platform, timestamp;
            Button updateButton, deleteButton;

            public GameRequestViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.game_title);
                description = itemView.findViewById(R.id.game_description);
                platform = itemView.findViewById(R.id.game_platform);
                timestamp = itemView.findViewById(R.id.game_timestamp);
                updateButton = itemView.findViewById(R.id.update_button);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }
    }
}