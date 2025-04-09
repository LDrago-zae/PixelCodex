package com.example.pixelcodex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity3 extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView hamburgerMenuButton = findViewById(R.id.hamburgerMenuButton);

        // Handle Menu Button Click to Open Drawer
        hamburgerMenuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

//         Initialize NavigationView (optional: if you want to handle clicks later)
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String itemName = Objects.requireNonNull(item.getTitle()).toString();
            Toast.makeText(MainActivity3.this, itemName + " clicked", Toast.LENGTH_SHORT).show();

            // Handle item clicks later
            if (itemId == R.id.nav_wishlist) {
                // Load WishlistFragment
                findViewById(R.id.main).setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                loadFragment(new WishlistFragment());
            } else if (itemId == R.id.nav_logout) {
                showLogoutConfirmation();
            }

            // Close the drawer after selection
            drawerLayout.closeDrawer(GravityCompat.START);


            return true;
        });

        // Initialize ViewPager2
        ViewPager2 featuredViewPager = findViewById(R.id.featuredViewPager);

        // Sample game data for featured games
        List<Game> gameList = new ArrayList<>();
        gameList.add(new Game("Elden Ring", R.drawable.eldenring));
        gameList.add(new Game("Cyberpunk 2077", R.drawable.cyberpunk));
        gameList.add(new Game("Call of Duty", R.drawable.ghost));

        // Set up adapter for featured games with click listener
        FeaturedAdapter adapter = new FeaturedAdapter(gameList, this::openGameDetailsFragment);
        featuredViewPager.setAdapter(adapter);

        // Initialize "New on Codex" RecyclerView
        RecyclerView newOnCodexRecyclerView = findViewById(R.id.newOnSteamRecyclerView);

        // Sample data for "New on Codex" games
        List<CodexGame> newCodexGames = new ArrayList<>();
        newCodexGames.add(new CodexGame("Hades", "Roguelike action game", "$24.99", R.drawable.hades));
        newCodexGames.add(new CodexGame("Days Gone", "Open-world adventure", "$59.99", R.drawable.daysgone));
        newCodexGames.add(new CodexGame("Witcher 3", "Fantasy RPG", "$59.99", R.drawable.witcher3));

        // Set horizontal layout for "New on Codex" RecyclerView
        newOnCodexRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Set adapter for "New on Codex" RecyclerView
        CodexGameAdapter newCodexAdapter = new CodexGameAdapter(newCodexGames);
        newOnCodexRecyclerView.setAdapter(newCodexAdapter);

        // Initialize "Recommended for You" RecyclerView
        RecyclerView recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView);

        // Sample data for "Recommended" games
        List<CodexGame> recommendedGames = new ArrayList<>();
        recommendedGames.add(new CodexGame("Red Dead Redemption 2", "Open world western", "$39.99", R.drawable.rdr2));
        recommendedGames.add(new CodexGame("Horizon Zero Dawn", "Action RPG", "$29.99", R.drawable.horizon));
        recommendedGames.add(new CodexGame("God of War", "Action adventure", "$49.99", R.drawable.god2));

        // Set horizontal layout manager for "Recommended" RecyclerView
        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Create and set adapter for "Recommended" RecyclerView
        CodexGameAdapter recommendedAdapter = new CodexGameAdapter(recommendedGames);
        recommendedRecyclerView.setAdapter(recommendedAdapter);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        // Handle BottomNavigationView item clicks
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    // Clear back stack and show home screen
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    findViewById(R.id.main).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragment_container).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.main).setVisibility(View.GONE);
                    findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

                    // Set the fragment to load based on the selected item
                    if (itemId == R.id.nav_search) {
                        selectedFragment = new SearchFragment();
                    } else if (itemId == R.id.nav_news) {
                        selectedFragment = new AddFragment();
                    } else if (itemId == R.id.nav_plus) {
                        selectedFragment = new NewsFragment();
                    } else if (itemId == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    if (selectedFragment != null) {
                        loadFragment(selectedFragment);
                    }
                }
                return true;
            }
        });

    }

    // Open game details fragment when a game is clicked
    private void openGameDetailsFragment(Game game) {
        GameDetailsFragment fragment = GameDetailsFragment.newInstance(game.title, game.imageResId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Method to replace fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    // Adapter for ViewPager2 (Featured Games)
    static class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {

        private final List<Game> games;
        private final OnGameClickListener listener;

        public FeaturedAdapter(List<Game> games, OnGameClickListener listener) {
            this.games = games;
            this.listener = listener;
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


            // Handle click event
            holder.itemView.setOnClickListener(v -> listener.onGameClick(game));
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

        // Interface for handling game click events
        interface OnGameClickListener {
            void onGameClick(Game game);
        }
    }

    private void showLogoutConfirmation() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set title and message
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?");

        // Set the "Yes" button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle logout logic here
                Toast.makeText(MainActivity3.this, "You have logged out.", Toast.LENGTH_SHORT).show();
                finish(); // Close the current activity (or navigate to login screen)
            }
        });

        // Set the "No" button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set the background of the dialog to the rounded drawable
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.alert_dialogue_rounded_background);

        // Show the dialog
        dialog.show();
    }

}