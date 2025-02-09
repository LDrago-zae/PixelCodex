package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private ViewPager2 featuredViewPager;
    private List<Game> gameList;
    private FeaturedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Initialize ViewPager2
        featuredViewPager = findViewById(R.id.featuredViewPager);

        // Sample game data
        gameList = new ArrayList<>();
        gameList.add(new Game("Elden Ring", R.drawable.eldenring));
        gameList.add(new Game("Cyberpunk 2077", R.drawable.cyberpunk));
        gameList.add(new Game("The Witcher 3", R.drawable.ghost));

        // Set up adapter
        adapter = new FeaturedAdapter(gameList);
        featuredViewPager.setAdapter(adapter);
    }

    // Game model class
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
