package com.example.pixelcodex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GameDetailsFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_IMAGE = "imageResId";

    private String gameTitle;
    private int gameImageResId;
    private DatabaseHelper dbHelper;
    private DatabaseReference gamesRef;
    private ImageView bookmarkIcon;
    private ImageView gameImageView;
    private TextView descriptionTextView;
    private TextView minDetails;
    private TextView recDetails;
    private BroadcastReceiver networkReceiver;

    public static GameDetailsFragment newInstance(String title, int imageResId) {
        GameDetailsFragment fragment = new GameDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_IMAGE, imageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameTitle = getArguments().getString(ARG_TITLE);
            gameImageResId = getArguments().getInt(ARG_IMAGE);
        }
        dbHelper = new DatabaseHelper(requireContext());
        gamesRef = FirebaseDatabase.getInstance().getReference("games");

        // Enable offline persistence
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_details, container, false);

        TextView titleTextView = view.findViewById(R.id.gameTitle);
        gameImageView = view.findViewById(R.id.gameImage);
        descriptionTextView = view.findViewById(R.id.gameDescription);
        minDetails = view.findViewById(R.id.minimumDetails);
        recDetails = view.findViewById(R.id.recommendedDetails);
        bookmarkIcon = view.findViewById(R.id.bookmarkIcon);
        ImageView backButton = view.findViewById(R.id.backButton);

        // Set the title immediately
        titleTextView.setText(gameTitle);

        // Fetch data from Realtime Database or save if not exists
        fetchOrSaveGameData();

        // Set up network change listener to refresh data when online
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fetchOrSaveGameData();
            }
        };
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        requireContext().registerReceiver(networkReceiver, filter);

        // Set up bookmark icon state
        updateBookmarkIcon();

        // Handle bookmark icon click
        bookmarkIcon.setOnClickListener(v -> {
            if (dbHelper.isGameInWishlist(gameTitle)) {
                dbHelper.removeGameFromWishlist(gameTitle);
                Toast.makeText(requireContext(), gameTitle + " removed from wishlist", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addGameToWishlist(gameTitle, gameImageResId);
                Toast.makeText(requireContext(), gameTitle + " added to wishlist", Toast.LENGTH_SHORT).show();
            }
            updateBookmarkIcon();
        });

        // Set up Minimum Requirements section
        LinearLayout minHeader = view.findViewById(R.id.minHeader);
        ImageView minArrow = view.findViewById(R.id.minArrow);

        minHeader.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) minHeader.getParent(), new AutoTransition());
            if (minDetails.getVisibility() == View.GONE) {
                minDetails.setVisibility(View.VISIBLE);
                minArrow.setRotation(180);
            } else {
                minDetails.setVisibility(View.GONE);
                minArrow.setRotation(0);
            }
        });

        // Back button functionality
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Set up Recommended Requirements section
        LinearLayout recHeader = view.findViewById(R.id.recHeader);
        ImageView recArrow = view.findViewById(R.id.recArrow);

        recHeader.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) recHeader.getParent(), new AutoTransition());
            if (recDetails.getVisibility() == View.GONE) {
                recDetails.setVisibility(View.VISIBLE);
                recArrow.setRotation(180);
            } else {
                recDetails.setVisibility(View.GONE);
                recArrow.setRotation(0);
            }
        });

        return view;
    }

    private void fetchOrSaveGameData() {
        DatabaseReference gameRef = gamesRef.child(gameTitle.replace(" ", "_"));
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Data exists in Firebase, use it to update the UI
                    String title = snapshot.child("title").getValue(String.class);
                    Integer imageResId = snapshot.child("imageResId").getValue(Integer.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String minimumRequirements = snapshot.child("minimumRequirements").getValue(String.class);
                    String recommendedRequirements = snapshot.child("recommendedRequirements").getValue(String.class);

                    updateUI(imageResId != null ? imageResId : gameImageResId, description, minimumRequirements, recommendedRequirements);
                } else {
                    // Data does not exist in Firebase, save the hardcoded data and then update the UI
                    saveGameDataToFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to fetch game data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // Fallback to hardcoded data
                loadHardcodedData();
            }
        });
    }

    private void saveGameDataToFirebase() {
        Map<String, Object> gameData = new HashMap<>();
        gameData.put("title", gameTitle);
        gameData.put("imageResId", gameImageResId);

        // Set game-specific details using switch case
        switch (gameTitle) {
            case "Elden Ring":
                gameData.put("description", "An open-world action RPG developed by FromSoftware and Bandai Namco Entertainment. Explore vast landscapes and battle formidable enemies.");
                gameData.put("minimumRequirements", "OS: Windows 10\nProcessor: Intel Core i5-8400\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970");
                gameData.put("recommendedRequirements", "OS: Windows 10\nProcessor: Intel Core i7-9700K\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2060");
                break;
            case "Cyberpunk 2077":
                gameData.put("description", "An open-world, action-adventure story set in Night City, a megalopolis obsessed with power, glamour, and body modification.");
                gameData.put("minimumRequirements", "OS: Windows 7 64-bit\nProcessor: Intel Core i5-3570\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4790\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2060");
                break;
            case "Call of Duty":
                gameData.put("description", "A first-person shooter game developed by Infinity Ward, featuring intense multiplayer action and a gripping campaign.");
                gameData.put("minimumRequirements", "OS: Windows 7 64-bit\nProcessor: Intel Core i3-4340\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 670");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 970");
                break;
            case "Hades":
                gameData.put("description", "A rogue-like dungeon crawler with a unique narrative structure, where you play as Zagreus, the son of Hades.");
                gameData.put("minimumRequirements", "OS: Windows 7\nProcessor: Intel Core i5-2400\nMemory: 4 GB RAM\nGraphics: NVIDIA GTX 650");
                gameData.put("recommendedRequirements", "OS: Windows 10\nProcessor: Intel Core i7-3770\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 960");
                break;
            case "God of War":
                gameData.put("description", "An action-adventure game that follows Kratos and his son Atreus as they journey through Norse mythology.");
                gameData.put("minimumRequirements", "OS: Windows 10\nProcessor: Intel Core i5-2500K\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 660");
                gameData.put("recommendedRequirements", "OS: Windows 10\nProcessor: Intel Core i7-4770K\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 970");
                break;
            case "Days Gone":
                gameData.put("description", "An open-world action-adventure game set in a post-apocalyptic world overrun by freakers.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 1060");
                break;
            case "Witcher 3":
                gameData.put("description", "A fantasy RPG set in a vast open world, following the witcher Geralt of Rivia.");
                gameData.put("minimumRequirements", "OS: Windows 7 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 6 GB RAM\nGraphics: NVIDIA GTX 660");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-3770\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970");
                break;
            case "Red Dead Redemption 2":
                gameData.put("description", "An epic tale of life in America’s unforgiving heartland, featuring a vast open world.");
                gameData.put("minimumRequirements", "OS: Windows 7 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1060");
                break;
            case "Horizon Zero Dawn":
                gameData.put("description", "An action RPG where you play as Aloy in a lush world inhabited by robotic creatures.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 1070");
                break;
            default:
                gameData.put("description", "Welcome to San Francisco, the birthplace of the technological revolution. Play as young Marcus, a brilliant hacker, and join DedSec, the most celebrated hacker group. Your goal: the largest hacking operation in history.");
                gameData.put("minimumRequirements", "No minimum requirements available.");
                gameData.put("recommendedRequirements", "No recommended requirements available.");
                break;
        }

        // Save to Firebase
        gamesRef.child(gameTitle.replace(" ", "_")).setValue(gameData)
                .addOnSuccessListener(aVoid -> {
                    // After saving, update the UI with the saved data
                    updateUI(gameImageResId, (String) gameData.get("description"),
                            (String) gameData.get("minimumRequirements"),
                            (String) gameData.get("recommendedRequirements"));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save game data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Fallback to hardcoded data
                    loadHardcodedData();
                });
    }

    private void loadHardcodedData() {
        // Fallback to hardcoded data if Firebase fails
        int imageResId = gameImageResId;
        String description;
        String minimumRequirements;
        String recommendedRequirements;

        switch (gameTitle) {
            case "Elden Ring":
                imageResId = R.drawable.eldenring;
                description = "An open-world action RPG developed by FromSoftware and Bandai Namco Entertainment. Explore vast landscapes and battle formidable enemies.";
                minimumRequirements = "OS: Windows 10\nProcessor: Intel Core i5-8400\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970";
                recommendedRequirements = "OS: Windows 10\nProcessor: Intel Core i7-9700K\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2060";
                break;
            case "Cyberpunk 2077":
                imageResId = R.drawable.cyberpunk;
                description = "An open-world, action-adventure story set in Night City, a megalopolis obsessed with power, glamour, and body modification.";
                minimumRequirements = "OS: Windows 7 64-bit\nProcessor: Intel Core i5-3570\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4790\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2060";
                break;
            case "Call of Duty":
                imageResId = R.drawable.ghost;
                description = "A first-person shooter game developed by Infinity Ward, featuring intense multiplayer action and a gripping campaign.";
                minimumRequirements = "OS: Windows 7 64-bit\nProcessor: Intel Core i3-4340\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 670";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 970";
                break;
            case "Hades":
                imageResId = R.drawable.hades;
                description = "A rogue-like dungeon crawler with a unique narrative structure, where you play as Zagreus, the son of Hades.";
                minimumRequirements = "OS: Windows 7\nProcessor: Intel Core i5-2400\nMemory: 4 GB RAM\nGraphics: NVIDIA GTX 650";
                recommendedRequirements = "OS: Windows 10\nProcessor: Intel Core i7-3770\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 960";
                break;
            case "God of War":
                imageResId = R.drawable.god2;
                description = "An action-adventure game that follows Kratos and his son Atreus as they journey through Norse mythology.";
                minimumRequirements = "OS: Windows 10\nProcessor: Intel Core i5-2500K\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 660";
                recommendedRequirements = "OS: Windows 10\nProcessor: Intel Core i7-4770K\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 970";
                break;
            case "Days Gone":
                imageResId = R.drawable.daysgone;
                description = "An open-world action-adventure game set in a post-apocalyptic world overrun by freakers.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 1060";
                break;
            case "Witcher 3":
                imageResId = R.drawable.witcher3;
                description = "A fantasy RPG set in a vast open world, following the witcher Geralt of Rivia.";
                minimumRequirements = "OS: Windows 7 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 6 GB RAM\nGraphics: NVIDIA GTX 660";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-3770\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970";
                break;
            case "Red Dead Redemption 2":
                imageResId = R.drawable.rdr2;
                description = "An epic tale of life in America’s unforgiving heartland, featuring a vast open world.";
                minimumRequirements = "OS: Windows 7 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1060";
                break;
            case "Horizon Zero Dawn":
                imageResId = R.drawable.horizon;
                description = "An action RPG where you play as Aloy in a lush world inhabited by robotic creatures.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 1070";
                break;
            default:
                description = "Welcome to San Francisco, the birthplace of the technological revolution. Play as young Marcus, a brilliant hacker, and join DedSec, the most celebrated hacker group. Your goal: the largest hacking operation in history.";
                minimumRequirements = "No minimum requirements available.";
                recommendedRequirements = "No recommended requirements available.";
                break;
        }

        updateUI(imageResId, description, minimumRequirements, recommendedRequirements);
    }

    private void updateUI(int imageResId, String description, String minimumRequirements, String recommendedRequirements) {
        // Update the UI with the provided data
        Glide.with(this)
                .load(imageResId)
                .centerCrop()
                .into(gameImageView);
        descriptionTextView.setText(description);
        minDetails.setText(minimumRequirements);
        recDetails.setText(recommendedRequirements);
    }

    private void updateBookmarkIcon() {
        if (dbHelper.isGameInWishlist(gameTitle)) {
            bookmarkIcon.setImageResource(R.drawable.favourite_filled);
        } else {
            bookmarkIcon.setImageResource(R.drawable.favorite2);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (networkReceiver != null) {
            requireContext().unregisterReceiver(networkReceiver);
        }
    }
}