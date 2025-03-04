package com.example.pixelcodex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeaturedAdaptor extends RecyclerView.Adapter<FeaturedAdaptor.ViewHolder> {

    private final List<FeaturedGame> featuredGames;

    public FeaturedAdaptor(List<FeaturedGame> featuredGames) {
        this.featuredGames = featuredGames;
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
}
