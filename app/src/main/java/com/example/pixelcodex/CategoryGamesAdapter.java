package com.example.pixelcodex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryGamesAdapter extends RecyclerView.Adapter<CategoryGamesAdapter.GameViewHolder> {

    private List<Game> games;
    private final Context context;
    private final SearchResultsAdapter.OnGameClickListener listener;

    public CategoryGamesAdapter(Context context, List<Game> games, SearchResultsAdapter.OnGameClickListener listener) {
        this.context = context;
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_game_card, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameTitle.setText(game.getTitle());
        holder.gameImage.setImageResource(game.getImageResId());
        holder.itemView.setOnClickListener(v -> listener.onGameClick(game));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void updateGames(List<Game> newGames) {
        this.games = newGames;
        notifyDataSetChanged();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameTitle;
        ImageView gameImage;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameTitle = itemView.findViewById(R.id.gameTitle);
            gameImage = itemView.findViewById(R.id.gameImage);
        }
    }
}