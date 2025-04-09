package com.example.pixelcodex;

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

public class GameDetailsFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_IMAGE = "imageResId";

    private String gameTitle;
    private int gameImageResId;
    private DatabaseHelper dbHelper;
    private ImageView bookmarkIcon;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_details, container, false);

        TextView titleTextView = view.findViewById(R.id.gameTitle);
        ImageView gameImageView = view.findViewById(R.id.gameImage);
        TextView descriptionTextView = view.findViewById(R.id.gameDescription);
        bookmarkIcon = view.findViewById(R.id.bookmarkIcon);
        ImageView backButton = view.findViewById(R.id.backButton);

        // Set game data
        titleTextView.setText(gameTitle);
        gameImageView.setImageResource(gameImageResId);
        descriptionTextView.setText("Welcome to San Francisco, the birthplace of the technological revolution. "
                + "Play as young Marcus, a brilliant hacker, and join DedSec, the most celebrated hacker group. "
                + "Your goal: the largest hacking operation in history.");

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

        // Handle back button click
        backButton.setOnClickListener(v -> {
            // Pop the current fragment from the back stack to go back
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Set up Minimum Requirements section
        LinearLayout minHeader = view.findViewById(R.id.minHeader);
        TextView minDetails = view.findViewById(R.id.minimumDetails);
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
        TextView recDetails = view.findViewById(R.id.recommendedDetails);
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

    private void updateBookmarkIcon() {
        if (dbHelper.isGameInWishlist(gameTitle)) {
            bookmarkIcon.setImageResource(R.drawable.favourite_filled); // Filled bookmark icon
        } else {
            bookmarkIcon.setImageResource(R.drawable.favorite2); // Unfilled bookmark icon
        }
    }
}