package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeaturedAdaptor extends RecyclerView.Adapter<FeaturedAdaptor.ViewHolder> {

    private final List<FeaturedGame> featuredGames;
    private final OnGameClickListener listener;

    // Constructor with a click listener
    public FeaturedAdaptor(List<FeaturedGame> featuredGames, OnGameClickListener listener) {
        this.featuredGames = featuredGames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeaturedGame game = featuredGames.get(position);
        holder.featuredTitle.setText(game.getTitle());
        holder.featuredImage.setImageResource(game.getImageResId());

        // Set click listener for each item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGameClick(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return featuredGames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView featuredImage;
        TextView featuredTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            featuredImage = itemView.findViewById(R.id.featuredImage);
            featuredTitle = itemView.findViewById(R.id.featuredTitle);
        }
    }

    // Interface for click listener
    public interface OnGameClickListener {
        void onGameClick(FeaturedGame game);
    }
}
