package com.example.pixelcodex;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity3 extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FloatingActionButton geminiFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView hamburgerMenuButton = findViewById(R.id.hamburgerMenuButton);
        bottomNavigationView = findViewById(R.id.bottomNav);
        geminiFab = findViewById(R.id.geminiFab);
        LinearLayout mainLayout = findViewById(R.id.main);

        geminiFab.setImageTintList(null);

        // Handle Menu Button Click to Open Drawer
        hamburgerMenuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Initialize NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String itemName = Objects.requireNonNull(item.getTitle()).toString();
            Toast.makeText(MainActivity3.this, itemName + " clicked", Toast.LENGTH_SHORT).show();

            if (itemId == R.id.nav_wishlist) {
                findViewById(R.id.main).setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                geminiFab.setVisibility(View.GONE); // Hide FAB when not on home screen
                loadFragment(new WishlistFragment());
            } else if (itemId == R.id.nav_game_request) {
                findViewById(R.id.main).setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                geminiFab.setVisibility(View.GONE); // Hide FAB when not on home screen
                loadFragment(new GameRequestsFragment());
            } else if (itemId == R.id.nav_settings) {
                findViewById(R.id.main).setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                geminiFab.setVisibility(View.GONE); // Hide FAB when not on home screen
                loadFragment(new SettingsFragment());
            } else if (itemId == R.id.nav_logout) {
                showLogoutConfirmation();
            } else if (itemId == R.id.nav_about_us) {
                findViewById(R.id.main).
                        setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                geminiFab.setVisibility(View.GONE); // Hide FAB when not on home screen
                loadFragment(new AboutUsFragment());
            }

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

        // Set up FAB click listener to open Gemini chat
        geminiFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeminiChatFragment GeminiChatFragment = new GeminiChatFragment();
                GeminiChatFragment.show(getSupportFragmentManager(), "GeminiChatFragment");
            }
        });

        // Handle BottomNavigationView item clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Clear back stack and show home screen
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                findViewById(R.id.main).setVisibility(View.VISIBLE);
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                geminiFab.setVisibility(View.VISIBLE); // Show FAB on home screen
            } else {
                findViewById(R.id.main).setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                geminiFab.setVisibility(View.GONE); // Hide FAB when not on home screen

                Fragment selectedFragment = getSelectedFragment(itemId);

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
            }
            return true;
        });

        // Ensure the home screen is visible on startup
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            geminiFab.setVisibility(View.VISIBLE); // Show FAB on initial load
        }

        // Set up back press handling using OnBackPressedDispatcher
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Check if the drawer is open
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }

                // Check the fragment back stack
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                if (backStackCount > 0) {
                    // Pop the top fragment from the back stack
                    getSupportFragmentManager().popBackStack();

                    // If the back stack is now empty, return to the home screen
                    if (backStackCount == 1) {
                        findViewById(R.id.main).setVisibility(View.VISIBLE);
                        findViewById(R.id.fragment_container).setVisibility(View.GONE);
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        geminiFab.setVisibility(View.VISIBLE); // Show FAB on home screen
                    }
                } else {
                    // No fragments in the back stack, finish the activity
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    private static Fragment getSelectedFragment(int itemId) {
        Fragment selectedFragment = null;
        if (itemId == R.id.nav_search) {
            selectedFragment = new SearchFragment();
        } else if (itemId == R.id.nav_news) {
            selectedFragment = new AddFragment();
        } else if (itemId == R.id.nav_plus) {
            selectedFragment = new NewsFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }
        return selectedFragment;
    }

    // Open game details fragment when a game is clicked
    private void openGameDetailsFragment(Game game) {
        findViewById(R.id.main).setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        geminiFab.setVisibility(View.GONE); // Hide FAB when not on home screen

        GameDetailsFragment fragment = GameDetailsFragment.newInstance(game.title, game.imageResId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Method to replace fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
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

        interface OnGameClickListener {
            void onGameClick(Game game);
        }
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Perform logout action
            mAuth.signOut();
            Toast.makeText(MainActivity3.this, "You have logged out.", Toast.LENGTH_SHORT).show();
            // Navigate to MainActivity (login screen) and clear the activity stack
            Intent intent = new Intent(MainActivity3.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.alert_dialogue_rounded_background);
        dialog.show();
    }
}