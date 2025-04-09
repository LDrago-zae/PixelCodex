package com.example.pixelcodex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final List<Game> gameList;
    private DatabaseHelper dbHelper;

    public WishlistAdapter(List<Game> gameList) {
        this.gameList = gameList;
    }

    // Method to set the DatabaseHelper
    public void setDatabaseHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Game game = gameList.get(position);
        holder.gameTitle.setText(game.getTitle());
        holder.gameImage.setImageResource(game.getImageResId());

        // Set OnClickListener for the remove button
        holder.removeButton.setOnClickListener(v -> {
            if (dbHelper == null) {
                Toast.makeText(holder.itemView.getContext(), "Error: Database not initialized", Toast.LENGTH_SHORT).show();
                return;
            }

            // Remove the game from the database
            dbHelper.removeGameFromWishlist(game.getTitle());

            // Remove the game from the list and update the RecyclerView
            gameList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, gameList.size());

            // Show a Toast message
            Toast.makeText(holder.itemView.getContext(), game.getTitle() + " removed from wishlist", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void removeItem(int position) {
        gameList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, gameList.size());
    }

    public Game getItem(int position) {
        return gameList.get(position);
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView gameTitle;
        ImageView gameImage;
        Button removeButton;

        WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            gameTitle = itemView.findViewById(R.id.gameTitle);
            gameImage = itemView.findViewById(R.id.gameImage);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}