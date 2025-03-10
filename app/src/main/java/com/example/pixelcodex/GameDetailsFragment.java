package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GameDetailsFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_IMAGE = "imageResId";

    private String gameTitle;
    private int gameImageResId;

    // ✅ Create a newInstance method to pass data to the fragment
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_details, container, false);

        TextView titleTextView = view.findViewById(R.id.gameTitle);
        ImageView gameImageView = view.findViewById(R.id.gameImage);

        // Set the game details
        titleTextView.setText(gameTitle);
        gameImageView.setImageResource(gameImageResId);

        return view;
    }
}
