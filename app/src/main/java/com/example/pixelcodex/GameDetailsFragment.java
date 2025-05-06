package com.example.pixelcodex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
    private TextView trailerUrlTextView;
    private TextView gameplayUrlTextView;
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
        trailerUrlTextView = view.findViewById(R.id.trailerUrl);
        gameplayUrlTextView = view.findViewById(R.id.gameplayUrl);
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

        // Set up Trailer section
        LinearLayout trailerHeader = view.findViewById(R.id.trailerHeader);
        ImageView trailerArrow = view.findViewById(R.id.trailerArrow);

        trailerHeader.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) trailerHeader.getParent(), new AutoTransition());
            if (trailerUrlTextView.getVisibility() == View.GONE) {
                trailerUrlTextView.setVisibility(View.VISIBLE);
                trailerArrow.setRotation(180);
            } else {
                trailerUrlTextView.setVisibility(View.GONE);
                trailerArrow.setRotation(0);
            }
        });

        // Set up Gameplay section
        LinearLayout gameplayHeader = view.findViewById(R.id.gameplayHeader);
        ImageView gameplayArrow = view.findViewById(R.id.gameplayArrow);

        gameplayHeader.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) gameplayHeader.getParent(), new AutoTransition());
            if (gameplayUrlTextView.getVisibility() == View.GONE) {
                gameplayUrlTextView.setVisibility(View.VISIBLE);
                gameplayArrow.setRotation(180);
            } else {
                gameplayUrlTextView.setVisibility(View.GONE);
                gameplayArrow.setRotation(0);
            }
        });

        // Back button functionality
        backButton.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

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
                    String trailerUrl = snapshot.child("trailerUrl").getValue(String.class);
                    String gameplayUrl = snapshot.child("gameplayUrl").getValue(String.class);

                    updateUI(imageResId != null ? imageResId : gameImageResId, description, minimumRequirements, recommendedRequirements, trailerUrl, gameplayUrl);
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
                gameData.put("description", "A fantasy action RPG from FromSoftware, set in the Lands Between. Collaborate with George R.R. Martin to battle mythical creatures and uncover the secrets of the Elden Ring.");
                gameData.put("minimumRequirements", "OS: Windows 10\nProcessor: Intel Core i5-8400 / AMD Ryzen 3 3300X\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1060 3GB / AMD RX 580 4GB\nStorage: 60 GB");
                gameData.put("recommendedRequirements", "OS: Windows 11\nProcessor: Intel Core i7-8700K / AMD Ryzen 5 3600X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2070 8GB / AMD RX 6700 XT\nStorage: 60 GB");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=E3Huy2cdih0&t=3s");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=JldMvQMO_5U");
                break;
            case "Cyberpunk 2077":
                gameData.put("description", "An open-world RPG set in the dystopian Night City, where you assume the first-person perspective of V, a customizable mercenary, in a world of cybernetic enhancements and corporate conspiracies.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-9700 / AMD Ryzen 5 3600\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 970 / AMD RX 6700 XT\nStorage: 70 GB SSD");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-12700 / AMD Ryzen 7 7800X3D\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 4060 Ti / AMD RX 7900 XT\nStorage: 70 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=8X2kIfS6fb8");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=WX1hAbGmdwg");
                break;
            case "Call of Duty":
                gameData.put("description", "A fast-paced first-person shooter by Infinity Ward, featuring intense multiplayer battles and a cinematic campaign set in modern warfare scenarios.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i3-4340 / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1650 / AMD RX 470\nStorage: 175 GB");
                gameData.put("recommendedRequirements", "OS: Windows 11 64-bit\nProcessor: Intel Core i5-6600K / AMD Ryzen 5 1600\nMemory: 12 GB RAM\nGraphics: NVIDIA RTX 3060 / AMD RX 6600 XT\nStorage: 175 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=0E44DClsX5Q");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=KlCzROTAos4");
                break;
            case "Hades":
                gameData.put("description", "A rogue-like action game by Supergiant Games, where you play as Zagreus, son of Hades, escaping the Underworld with dynamic combat and a rich narrative.");
                gameData.put("minimumRequirements", "OS: Windows 7 SP1\nProcessor: Dual Core 2.4 GHz\nMemory: 4 GB RAM\nGraphics: 1GB VRAM / DirectX 10+ support\nStorage: 15 GB");
                gameData.put("recommendedRequirements", "OS: Windows 10\nProcessor: Dual Core 3.0 GHz\nMemory: 8 GB RAM\nGraphics: 2GB VRAM / DirectX 11+ support\nStorage: 15 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=91t0ha9x0AE");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=dPmMCzvLs9E&t=11379s");
                break;
            case "God of War":
                gameData.put("description", "An action-adventure epic by Santa Monica Studio, following Kratos and Atreus in a Norse mythology-inspired journey filled with brutal combat and emotional storytelling.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K / AMD Ryzen 3 1200\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 960 4GB / AMD R9 290X\nStorage: 70 GB");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-6600K / AMD Ryzen 5 2400G\nMemory: 8 GB RAM\nGraphics: NVIDIA RTX 2060 / AMD RX 5700\nStorage: 70 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=rClXqZD2Xrs");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=_oOZG5-tqpA");
                break;
            case "Days Gone":
                gameData.put("description", "An open-world survival game by Bend Studio, set in a post-apocalyptic Oregon where Deacon St. John battles hordes of Freakers on his motorcycle.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1060 / AMD RX 580\nStorage: 70 GB");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K / AMD Ryzen 5 1500X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2060 / AMD RX 6600\nStorage: 70 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=FKtaOY9lMvM");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=99NfMJFaG1Y");
                break;
            case "Witcher 3":
                gameData.put("description", "An epic RPG by CD Projekt, following Geralt of Rivia in a monster-filled open world with deep storytelling and choice-driven gameplay.");
                gameData.put("minimumRequirements", "OS: Windows 7 64-bit\nProcessor: Intel Core i5-2500K / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970 / AMD RX 470\nStorage: 35 GB");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-3770 / AMD Ryzen 5 1600\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 1070 / AMD RX 6800\nStorage: 35 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=1-l29HlKkXU");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=k08RRor_zPI");
                break;
            case "Red Dead Redemption 2":
                gameData.put("description", "An open-world western by Rockstar Games, chronicling Arthur Morgan’s journey with the Van der Linde gang in a beautifully detailed 1899 America.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1060 6GB / AMD RX 480\nStorage: 150 GB");
                gameData.put("recommendedRequirements", "OS: Windows 11 64-bit\nProcessor: Intel Core i7-4770K / AMD Ryzen 5 1500X\nMemory: 12 GB RAM\nGraphics: NVIDIA RTX 2070 / AMD RX 5700 XT\nStorage: 150 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=gmA6MrX81z4");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=AcOpiWppO3k");
                break;
            case "Horizon Zero Dawn":
                gameData.put("description", "An action RPG by Guerrilla Games, where Aloy explores a post-apocalyptic world of robotic creatures to uncover her destiny.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970 / AMD RX 470\nStorage: 100 GB");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K / AMD Ryzen 5 1500X\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 1060 6GB / AMD RX 580\nStorage: 100 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=u4-FCsiF5x4");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=yKLwbkbWQpc");
                break;
            case "Phantom Blade Zero":
                gameData.put("description", "An upcoming action RPG by S-Game, blending fast-paced combat and dark fantasy in a world inspired by Chinese mythology.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-8400 / AMD Ryzen 3 3300X\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1060 / AMD RX 580\nStorage: 60 GB (Estimated)");
                gameData.put("recommendedRequirements", "OS: Windows 11 64-bit\nProcessor: Intel Core i7-9700 / AMD Ryzen 5 5600X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 3060 / AMD RX 6700 XT\nStorage: 60 GB SSD (Estimated)");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=EZr1HXkEahs");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=HqW8RfCIMCQ");
                break;
            case "Final Fantasy VII":
                gameData.put("description", "A continuation of the FFVII Remake saga by Square Enix, following Cloud and his allies beyond Midgar in a reimagined classic RPG journey.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: AMD Ryzen 5 3400G / Intel Core i5-6400\nMemory: 8 GB RAM\nGraphics: AMD RX 570 / NVIDIA GTX 1650\nStorage: 100 GB");
                gameData.put("recommendedRequirements", "OS: Windows 11 64-bit\nProcessor: AMD Ryzen 7 5700X / Intel Core i7-9700\nMemory: 16 GB RAM\nGraphics: AMD RX 6700 XT / NVIDIA RTX 3060\nStorage: 100 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=Kznek1uNVsg");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=43Jt-GG1PFA");
                break;
            case "Grand Theft Auto VI":
                gameData.put("description", "An open-world crime epic by Rockstar Games, set in the fictional state of Leonida, featuring dual protagonists and a sprawling criminal underworld.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-8400 / AMD Ryzen 5 3600\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1660 / AMD RX 580\nStorage: 150 GB SSD (Estimated)");
                gameData.put("recommendedRequirements", "OS: Windows 11 64-bit\nProcessor: Intel Core i7-10700K / AMD Ryzen 7 5800X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 3070 / AMD RX 6800\nStorage: 150 GB SSD (Estimated)");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=QdBZY2fkU-0");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=QdBZY2fkU-0"); // Placeholder, as GTA VI gameplay isn't officially released yet
                break;
            case "Tides of Annihilation":
                gameData.put("description", "A fictional action RPG set in a post-apocalyptic world, where survival hinges on battling relentless enemies and uncovering hidden truths.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-6400 / AMD Ryzen 3 1200\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1650 / AMD RX 570\nStorage: 50 GB (Estimated)");
                gameData.put("recommendedRequirements", "OS: Windows 11 64-bit\nProcessor: Intel Core i7-7700 / AMD Ryzen 5 3600\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2060 / AMD RX 6600\nStorage: 50 GB SSD (Estimated)");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=JJ3fDz_xCio");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=YdFagFDMxKc");
                break;
            case "Onimusha":
                gameData.put("description", "A remastered action-adventure by Capcom, set in feudal Japan, where samurai Samanosuke battles supernatural forces with intense combat.");
                gameData.put("minimumRequirements", "OS: Windows 7 64-bit\nProcessor: Intel Core i3-2100 / AMD FX-6300\nMemory: 4 GB RAM\nGraphics: NVIDIA GTX 760 / AMD Radeon HD 7950\nStorage: 12 GB");
                gameData.put("recommendedRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-4670 / AMD Ryzen 3 1200\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970 / AMD RX 570\nStorage: 12 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=ZPvrzaC44pc");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=lOEipD62nSc");
                break;
            case "Prince of Persia: The Lost Crown":
                gameData.put("description", "A 2.5D action-adventure platformer by Ubisoft, where Sargon explores a mythological Persian world to rescue the Prince.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-4460 / AMD Ryzen 3 1200\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 950 / AMD RX 5500 XT\nStorage: 30 GB");
                gameData.put("recommendedRequirements", "OS: Windows 11 64-bit\nProcessor: Intel Core i7-6700 / AMD Ryzen 5 1600\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1060 / AMD RX 5600 XT\nStorage: 30 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=a3SS_o3FUsA");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=wWbf7Y9bmSk");
                break;
            case "Ghost of Yotie":
                gameData.put("description", "An open-world action-adventure by Sucker Punch, where Jin Sakai fights to liberate Tsushima Island from Mongol invaders in feudal Japan.");
                gameData.put("minimumRequirements", "OS: Windows 10 64-bit\nProcessor: Intel Core i5-8600 / AMD Ryzen 5 3600\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 2060 / AMD RX 5600 XT\nStorage: 75 GB SSD");
                gameData.put("recommendedRequirements", "OS: Windows 11 64-bit\nProcessor: Intel Core i7-10700K / AMD Ryzen 7 3700X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 3080 / AMD RX 6800\nStorage: 75 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=PVBLJYjSAhg");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=6iLxOnuWl1c");
                break;
            case "Elden Ring Nightreign":
                gameData.put("description", "A fictional expansion for Elden Ring by FromSoftware, exploring a dark realm with new challenges and lore.");
                gameData.put("minimumRequirements", "OS: Windows 10\nProcessor: Intel Core i5-8400 / AMD Ryzen 3 3300X\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1060 3GB / AMD RX 580 4GB\nStorage: 60 GB");
                gameData.put("recommendedRequirements", "OS: Windows 11\nProcessor: Intel Core i7-8700K / AMD Ryzen 5 3600X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2070 8GB / AMD RX 6700 XT\nStorage: 60 GB SSD");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=Djtsw5k_DNc&t=1s");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=LX7Sjw9f7Rw&t=86s");
                break;
            default:
                gameData.put("description", "No description available for this game.");
                gameData.put("minimumRequirements", "No minimum requirements available.");
                gameData.put("recommendedRequirements", "No recommended requirements available.");
                gameData.put("trailerUrl", "https://www.youtube.com/watch?v=NotAvailable");
                gameData.put("gameplayUrl", "https://www.youtube.com/watch?v=NotAvailable");
                break;
        }

        // Save to Firebase
        gamesRef.child(gameTitle.replace(" ", "_")).setValue(gameData)
                .addOnSuccessListener(aVoid -> {
                    // After saving, update the UI with the saved data
                    updateUI(gameImageResId, (String) gameData.get("description"),
                            (String) gameData.get("minimumRequirements"),
                            (String) gameData.get("recommendedRequirements"),
                            (String) gameData.get("trailerUrl"),
                            (String) gameData.get("gameplayUrl"));
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
        String trailerUrl;
        String gameplayUrl;

        switch (gameTitle) {
            case "Elden Ring":
                imageResId = R.drawable.eldenring;
                description = "A fantasy action RPG from FromSoftware, set in the Lands Between. Collaborate with George R.R. Martin to battle mythical creatures and uncover the secrets of the Elden Ring.";
                minimumRequirements = "OS: Windows 10\nProcessor: Intel Core i5-8400 / AMD Ryzen 3 3300X\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1060 3GB / AMD RX 580 4GB\nStorage: 60 GB";
                recommendedRequirements = "OS: Windows 11\nProcessor: Intel Core i7-8700K / AMD Ryzen 5 3600X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2070 8GB / AMD RX 6700 XT\nStorage: 60 GB";
                trailerUrl = "https://www.youtube.com/watch?v=E3Huy2cdih0&t=3s";
                gameplayUrl = "https://www.youtube.com/watch?v=JldMvQMO_5U";
                break;
            case "Cyberpunk 2077":
                imageResId = R.drawable.cyberpunk;
                description = "An open-world RPG set in the dystopian Night City, where you assume the first-person perspective of V, a customizable mercenary, in a world of cybernetic enhancements and corporate conspiracies.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-9700 / AMD Ryzen 5 3600\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 970 / AMD RX 6700 XT\nStorage: 70 GB SSD";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-12700 / AMD Ryzen 7 7800X3D\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 4060 Ti / AMD RX 7900 XT\nStorage: 70 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=8X2kIfS6fb8";
                gameplayUrl = "https://www.youtube.com/watch?v=WX1hAbGmdwg";
                break;
            case "Call of Duty":
                imageResId = R.drawable.ghost;
                description = "A fast-paced first-person shooter by Infinity Ward, featuring intense multiplayer battles and a cinematic campaign set in modern warfare scenarios.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i3-4340 / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1650 / AMD RX 470\nStorage: 175 GB";
                recommendedRequirements = "OS: Windows 11 64-bit\nProcessor: Intel Core i5-6600K / AMD Ryzen 5 1600\nMemory: 12 GB RAM\nGraphics: NVIDIA RTX 3060 / AMD RX 6600 XT\nStorage: 175 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=0E44DClsX5Q";
                gameplayUrl = "https://www.youtube.com/watch?v=KlCzROTAos4";
                break;
            case "Hades":
                imageResId = R.drawable.hades;
                description = "A rogue-like action game by Supergiant Games, where you play as Zagreus, son of Hades, escaping the Underworld with dynamic combat and a rich narrative.";
                minimumRequirements = "OS: Windows 7 SP1\nProcessor: Dual Core 2.4 GHz\nMemory: 4 GB RAM\nGraphics: 1GB VRAM / DirectX 10+ support\nStorage: 15 GB";
                recommendedRequirements = "OS: Windows 10\nProcessor: Dual Core 3.0 GHz\nMemory: 8 GB RAM\nGraphics: 2GB VRAM / DirectX 11+ support\nStorage: 15 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=91t0ha9x0AE";
                gameplayUrl = "https://www.youtube.com/watch?v=dPmMCzvLs9E&t=11379s";
                break;
            case "God of War":
                imageResId = R.drawable.god2;
                description = "An action-adventure epic by Santa Monica Studio, following Kratos and Atreus in a Norse mythology-inspired journey filled with brutal combat and emotional storytelling.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K / AMD Ryzen 3 1200\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 960 4GB / AMD R9 290X\nStorage: 70 GB";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-6600K / AMD Ryzen 5 2400G\nMemory: 8 GB RAM\nGraphics: NVIDIA RTX 2060 / AMD RX 5700\nStorage: 70 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=rClXqZD2Xrs";
                gameplayUrl = "https://www.youtube.com/watch?v=_oOZG5-tqpA";
                break;
            case "Days Gone":
                imageResId = R.drawable.daysgone;
                description = "An open-world survival game by Bend Studio, set in a post-apocalyptic Oregon where Deacon St. John battles hordes of Freakers on his motorcycle.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1060 / AMD RX 580\nStorage: 70 GB";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K / AMD Ryzen 5 1500X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2060 / AMD RX 6600\nStorage: 70 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=FKtaOY9lMvM";
                gameplayUrl = "https://www.youtube.com/watch?v=99NfMJFaG1Y";
                break;
            case "Witcher 3":
                imageResId = R.drawable.witcher3;
                description = "An epic RPG by CD Projekt, following Geralt of Rivia in a monster-filled open world with deep storytelling and choice-driven gameplay.";
                minimumRequirements = "OS: Windows 7 64-bit\nProcessor: Intel Core i5-2500K / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970 / AMD RX 470\nStorage: 35 GB";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-3770 / AMD Ryzen 5 1600\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 1070 / AMD RX 6800\nStorage: 35 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=1-l29HlKkXU";
                gameplayUrl = "https://www.youtube.com/watch?v=k08RRor_zPI";
                break;
            case "Red Dead Redemption 2":
                imageResId = R.drawable.rdr2;
                description = "An open-world western by Rockstar Games, chronicling Arthur Morgan’s journey with the Van der Linde gang in a beautifully detailed 1899 America.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1060 6GB / AMD RX 480\nStorage: 150 GB";
                recommendedRequirements = "OS: Windows 11 64-bit\nProcessor: Intel Core i7-4770K / AMD Ryzen 5 1500X\nMemory: 12 GB RAM\nGraphics: NVIDIA RTX 2070 / AMD RX 5700 XT\nStorage: 150 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=gmA6MrX81z4";
                gameplayUrl = "https://www.youtube.com/watch?v=AcOpiWppO3k";
                break;
            case "Horizon Zero Dawn":
                imageResId = R.drawable.horizon;
                description = "An action RPG by Guerrilla Games, where Aloy explores a post-apocalyptic world of robotic creatures to uncover her destiny.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-2500K / AMD FX-6300\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970 / AMD RX 470\nStorage: 100 GB";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i7-4770K / AMD Ryzen 5 1500X\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 1060 6GB / AMD RX 580\nStorage: 100 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=u4-FCsiF5x4";
                gameplayUrl = "https://www.youtube.com/watch?v=yKLwbkbWQpc";
                break;
            case "Phantom Blade Zero":
                imageResId = R.drawable.pb0;
                description = "An upcoming action RPG by S-Game, blending fast-paced combat and dark fantasy in a world inspired by Chinese mythology.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-8400 / AMD Ryzen 3 3300X\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1060 / AMD RX 580\nStorage: 60 GB (Estimated)";
                recommendedRequirements = "OS: Windows 11 64-bit\nProcessor: Intel Core i7-9700 / AMD Ryzen 5 5600X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 3060 / AMD RX 6700 XT\nStorage: 60 GB SSD (Estimated)";
                trailerUrl = "https://www.youtube.com/watch?v=EZr1HXkEahs";
                gameplayUrl = "https://www.youtube.com/watch?v=HqW8RfCIMCQ";
                break;
            case "Final Fantasy VII":
                imageResId = R.drawable.ffvii;
                description = "A continuation of the FFVII Remake saga by Square Enix, following Cloud and his allies beyond Midgar in a reimagined classic RPG journey.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: AMD Ryzen 5 3400G / Intel Core i5-6400\nMemory: 8 GB RAM\nGraphics: AMD RX 570 / NVIDIA GTX 1650\nStorage: 100 GB";
                recommendedRequirements = "OS: Windows 11 64-bit\nProcessor: AMD Ryzen 7 5700X / Intel Core i7-9700\nMemory: 16 GB RAM\nGraphics: AMD RX 6700 XT / NVIDIA RTX 3060\nStorage: 100 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=Kznek1uNVsg";
                gameplayUrl = "https://www.youtube.com/watch?v=43Jt-GG1PFA";
                break;
            case "Grand Theft Auto VI":
                imageResId = R.drawable.gta6;
                description = "An open-world crime epic by Rockstar Games, set in the fictional state of Leonida, featuring dual protagonists and a sprawling criminal underworld.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-8400 / AMD Ryzen 5 3600\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1660 / AMD RX 580\nStorage: 150 GB SSD (Estimated)";
                recommendedRequirements = "OS: Windows 11 64-bit\nProcessor: Intel Core i7-10700K / AMD Ryzen 7 5800X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 3070 / AMD RX 6800\nStorage: 150 GB SSD (Estimated)";
                trailerUrl = "https://www.youtube.com/watch?v=QdBZY2fkU-0";
                gameplayUrl = "https://www.youtube.com/watch?v=QdBZY2fkU-0"; // Placeholder, as GTA VI gameplay isn't officially released yet
                break;
            case "Tides of Annihilation":
                imageResId = R.drawable.tides;
                description = "A fictional action RPG set in a post-apocalyptic world, where survival hinges on battling relentless enemies and uncovering hidden truths.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-6400 / AMD Ryzen 3 1200\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1650 / AMD RX 570\nStorage: 50 GB (Estimated)";
                recommendedRequirements = "OS: Windows 11 64-bit\nProcessor: Intel Core i7-7700 / AMD Ryzen 5 3600\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2060 / AMD RX 6600\nStorage: 50 GB SSD (Estimated)";
                trailerUrl = "https://www.youtube.com/watch?v=JJ3fDz_xCio";
                gameplayUrl = "https://www.youtube.com/watch?v=YdFagFDMxKc";
                break;
            case "Onimusha":
                imageResId = R.drawable.onimusha;
                description = "A remastered action-adventure by Capcom, set in feudal Japan, where samurai Samanosuke battles supernatural forces with intense combat.";
                minimumRequirements = "OS: Windows 7 64-bit\nProcessor: Intel Core i3-2100 / AMD FX-6300\nMemory: 4 GB RAM\nGraphics: NVIDIA GTX 760 / AMD Radeon HD 7950\nStorage: 12 GB";
                recommendedRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-4670 / AMD Ryzen 3 1200\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 970 / AMD RX 570\nStorage: 12 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=ZPvrzaC44pc";
                gameplayUrl = "https://www.youtube.com/watch?v=lOEipD62nSc";
                break;
            case "Prince of Persia: The Lost Crown":
                imageResId = R.drawable.pop;
                description = "A 2.5D action-adventure platformer by Ubisoft, where Sargon explores a mythological Persian world to rescue the Prince.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-4460 / AMD Ryzen 3 1200\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 950 / AMD RX 5500 XT\nStorage: 30 GB";
                recommendedRequirements = "OS: Windows 11 64-bit\nProcessor: Intel Core i7-6700 / AMD Ryzen 5 1600\nMemory: 8 GB RAM\nGraphics: NVIDIA GTX 1060 / AMD RX 5600 XT\nStorage: 30 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=a3SS_o3FUsA";
                gameplayUrl = "https://www.youtube.com/watch?v=wWbf7Y9bmSk";
                break;
            case "Ghost of Yotie":
                imageResId = R.drawable.goy;
                description = "An open-world action-adventure by Sucker Punch, where Jin Sakai fights to liberate Tsushima Island from Mongol invaders in feudal Japan.";
                minimumRequirements = "OS: Windows 10 64-bit\nProcessor: Intel Core i5-8600 / AMD Ryzen 5 3600\nMemory: 16 GB RAM\nGraphics: NVIDIA GTX 2060 / AMD RX 5600 XT\nStorage: 75 GB SSD";
                recommendedRequirements = "OS: Windows 11 64-bit\nProcessor: Intel Core i7-10700K / AMD Ryzen 7 3700X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 3080 / AMD RX 6800\nStorage: 75 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=PVBLJYjSAhg";
                gameplayUrl = "https://www.youtube.com/watch?v=6iLxOnuWl1c";
                break;
            case "Elden Ring Nightreign":
                imageResId = R.drawable.eldenring;
                description = "A fictional expansion for Elden Ring by FromSoftware, exploring a dark realm with new challenges and lore.";
                minimumRequirements = "OS: Windows 10\nProcessor: Intel Core i5-8400 / AMD Ryzen 3 3300X\nMemory: 12 GB RAM\nGraphics: NVIDIA GTX 1060 3GB / AMD RX 580 4GB\nStorage: 60 GB";
                recommendedRequirements = "OS: Windows 11\nProcessor: Intel Core i7-8700K / AMD Ryzen 5 3600X\nMemory: 16 GB RAM\nGraphics: NVIDIA RTX 2070 8GB / AMD RX 6700 XT\nStorage: 60 GB SSD";
                trailerUrl = "https://www.youtube.com/watch?v=Djtsw5k_DNc&t=1s";
                gameplayUrl = "https://www.youtube.com/watch?v=LX7Sjw9f7Rw&t=86s";
                break;
            default:
                description = "No description available for this game.";
                minimumRequirements = "No minimum requirements available.";
                recommendedRequirements = "No recommended requirements available.";
                trailerUrl = "https://www.youtube.com/watch?v=NotAvailable";
                gameplayUrl = "https://www.youtube.com/watch?v=NotAvailable";
                break;
        }

        updateUI(imageResId, description, minimumRequirements, recommendedRequirements, trailerUrl, gameplayUrl);
    }

    private void updateUI(int imageResId, String description, String minimumRequirements, String recommendedRequirements, String trailerUrl, String gameplayUrl) {
        // Update the UI with the provided data
        Glide.with(this)
                .load(imageResId)
                .centerCrop()
                .into(gameImageView);
        descriptionTextView.setText(description);
        minDetails.setText(minimumRequirements);
        recDetails.setText(recommendedRequirements);

        // Set up trailer URL
        if (trailerUrl != null && !trailerUrl.equals("https://www.youtube.com/watch?v=NotAvailable")) {
            trailerUrlTextView.setText("Watch Trailer");
            trailerUrlTextView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                startActivity(intent);
            });
        } else {
            trailerUrlTextView.setText("Trailer Not Available");
            trailerUrlTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            trailerUrlTextView.setClickable(false);
        }

        // Set up gameplay URL
        if (gameplayUrl != null && !gameplayUrl.equals("https://www.youtube.com/watch?v=NotAvailable") && !gameplayUrl.equals("https://www.youtube.com/watch?v=NotAvailableYet")) {
            gameplayUrlTextView.setText("Watch Gameplay");
            gameplayUrlTextView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gameplayUrl));
                startActivity(intent);
            });
        } else {
            gameplayUrlTextView.setText("Gameplay Not Available");
            gameplayUrlTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            gameplayUrlTextView.setClickable(false);
        }
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