package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Initialize ViewPager2
        ViewPager2 featuredViewPager = findViewById(R.id.featuredViewPager);

        // Sample game data for featured games
        List<Game> gameList = new ArrayList<>();
        gameList.add(new Game("Elden Ring", R.drawable.eldenring));
        gameList.add(new Game("Cyberpunk 2077", R.drawable.cyberpunk));
        gameList.add(new Game("Call of Duty", R.drawable.ghost));

        // Set up adapter for featured games
        FeaturedAdapter adapter = new FeaturedAdapter(gameList);
        featuredViewPager.setAdapter(adapter);

        // Initialize "New on Codex" RecyclerView
        RecyclerView newOnCodexRecyclerView = findViewById(R.id.newOnSteamRecyclerView);

        // Sample data for "New on Codex" games
        List<CodexGame> newCodexGames = new ArrayList<>();
        newCodexGames.add(new CodexGame("Hades", "Roguelike action game", "$24.99", R.drawable.hades));
        newCodexGames.add(new CodexGame("Days Gone", "Space RPG", "$59.99", R.drawable.daysgone));
        newCodexGames.add(new CodexGame("Witcher 3", "Fantasy RPG", "$59.99", R.drawable.witcher3));

        // Set layout manager for "New on Codex" RecyclerView
        newOnCodexRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Create and set adapter for "New on Codex" RecyclerView
        CodexGameAdapter newCodexAdapter = new CodexGameAdapter(newCodexGames);
        newOnCodexRecyclerView.setAdapter(newCodexAdapter);

        // Initialize "Recommended for You" RecyclerView
        RecyclerView recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView);

        // Sample data for "Recommended" games (reusing same data for example)
        List<CodexGame> recommendedGames = new ArrayList<>();
        recommendedGames.add(new CodexGame("Red Dead Redemption 2", "Open world western", "$39.99", R.drawable.rdr2));
        recommendedGames.add(new CodexGame("Horizon Zero Dawn", "Action RPG", "$29.99", R.drawable.horizon));
        recommendedGames.add(new CodexGame("God of War", "Action adventure", "$49.99", R.drawable.god2));

        // Set horizontal layout manager for "Recommended" RecyclerView
        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Create and set adapter for "Recommended" RecyclerView
        CodexGameAdapter recommendedAdapter = new CodexGameAdapter(recommendedGames);
        recommendedRecyclerView.setAdapter(recommendedAdapter);
    }

    // Game model class for featured games
    static class Game {
        String title;
        int imageResId;

        public Game(String title, int imageResId) {
            this.title = title;
            this.imageResId = imageResId;
        }
    }

    // Adapter for ViewPager2
    static class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {

        private final List<Game> games;

        public FeaturedAdapter(List<Game> games) {
            this.games = games;
        }

        @NonNull
        @Override
        public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_view, parent, false);
            return new FeaturedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
            Game game = games.get(position);
            holder.featuredTitle.setText(game.title);
            holder.featuredImage.setImageResource(game.imageResId);
        }

        @Override
        public int getItemCount() {
            return games.size();
        }

        static class FeaturedViewHolder extends RecyclerView.ViewHolder {
            TextView featuredTitle;
            ImageView featuredImage;

            public FeaturedViewHolder(@NonNull View itemView) {
                super(itemView);
                featuredTitle = itemView.findViewById(R.id.featuredTitle);
                featuredImage = itemView.findViewById(R.id.featuredImage);
            }
        }
    }
}