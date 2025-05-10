package com.example.pixelcodex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CodexGameAdapter extends RecyclerView.Adapter<CodexGameAdapter.CodexGameViewHolder> {

    private final List<CodexGame> codexGames;
    private final OnGameClickListener listener;

    public CodexGameAdapter(List<CodexGame> codexGames, OnGameClickListener listener) {
        this.codexGames = codexGames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CodexGameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager2, parent, false);
        return new CodexGameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CodexGameViewHolder holder, int position) {
        CodexGame game = codexGames.get(position);
        holder.nameTextView.setText(game.getName());
//        holder.descriptionTextView.setText(game.getDescription());
        holder.priceTextView.setText(game.getPrice());
        holder.imageView.setImageResource(game.getImageResId());  // Use the image resource ID for displaying

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGameClick(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return codexGames.size();
    }

    // ViewHolder for each Codex game item
    public static class CodexGameViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        ImageView imageView;

        public CodexGameViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views based on your item layout
            nameTextView = itemView.findViewById(R.id.gameTitle);
//            descriptionTextView = itemView.findViewById(R.id.description);
            priceTextView = itemView.findViewById(R.id.gamePrice);
            imageView = itemView.findViewById(R.id.gameImage);
        }
    }

    public interface OnGameClickListener {
        void onGameClick(CodexGame game);
    }
}