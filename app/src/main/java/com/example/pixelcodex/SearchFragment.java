package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment implements SearchResultsAdapter.OnGameClickListener {

    private SearchView searchView;
    private RecyclerView categoryRecyclerView;
    private RecyclerView searchResultsRecyclerView;
    private RecyclerView categoryGamesRecyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private CategoryGamesAdapter categoryGamesAdapter;
    private List<Game> allGames;
    private List<Game> filteredGames;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize RecyclerViews
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        categoryGamesRecyclerView = view.findViewById(R.id.categoryGamesRecyclerView);

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Use horizontal layout for search results to match the featured games style
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryGamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)); // 2-column grid

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView);
        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchPlate.setBackground(null);
        setupSearchView();

        // Define categories
        List<String> categories = Arrays.asList(
                "New on Steam", "Adventure", "Action", "Casual", "First person shooter", "Single player", "Multiplayer", "Indie"
        );

        // Set category adapter
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categories);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Initialize game list for search
        allGames = new ArrayList<>();
        filteredGames = new ArrayList<>();
        populateGameList();

        // Set up search results adapter
        searchResultsAdapter = new SearchResultsAdapter(getContext(), filteredGames, this);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Set up category games adapter
        categoryGamesAdapter = new CategoryGamesAdapter(getContext(), new ArrayList<>(), this);
        categoryGamesRecyclerView.setAdapter(categoryGamesAdapter);

        // Set up category click listener
        categoryAdapter.setOnCategoryClickListener((position, categoryName) -> {
            // Handle category click to show category-specific games
            filterGamesByCategory(categoryName);
        });

        // show games when category is clicked
        categoryRecyclerView.setVisibility(View.VISIBLE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        categoryGamesRecyclerView.setVisibility(View.GONE);

        // Set up touch listener to dismiss keyboard when touching outside SearchView
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View focusedView = getActivity() != null ? getActivity().getCurrentFocus() : null;
                if (focusedView != null && focusedView.getWindowToken() != null) {
                    focusedView.clearFocus();
                }
            }
            v.performClick();
            return false;
        });

        // Dismiss keyboard when touching outside SearchView
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View focusedView = getActivity() != null ? getActivity().getCurrentFocus() : null;
                if (focusedView != null) {
                    focusedView.clearFocus();
                }
            }
            v.performClick();
            return false;
        });

        return view;
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(false);
        searchView.setFocusableInTouchMode(false);
        searchView.clearFocus();

        // Ensure the keyboard appears only when tapped
        searchView.setOnClickListener(v -> {
            searchView.setFocusableInTouchMode(true);
            searchView.setFocusable(true);
            searchView.requestFocus();
        });

        // Add search query listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterGames(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterGames(newText);
                return true;
            }
        });

        // Show/hide category list when search view is cleared
        searchView.setOnCloseListener(() -> {
            categoryRecyclerView.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.GONE);
            filteredGames.clear();
            searchResultsAdapter.notifyDataSetChanged();
            return false;
        });
    }

    private void populateGameList() {
        // Sample game data (same as in MainActivity3)
        allGames.add(new Game("Elden Ring", R.drawable.eldenring));
        allGames.add(new Game("Cyberpunk 2077", R.drawable.cyberpunk));
        allGames.add(new Game("Call of Duty", R.drawable.ghost));
        allGames.add(new Game("Hades", R.drawable.hades));
        allGames.add(new Game("Days Gone", R.drawable.daysgone));
        allGames.add(new Game("Witcher 3", R.drawable.witcher3));
        allGames.add(new Game("Red Dead Redemption 2", R.drawable.rdr2));
        allGames.add(new Game("Horizon Zero Dawn", R.drawable.horizon));
        allGames.add(new Game("God of War", R.drawable.god2));
        allGames.add(new Game("Ghost of Yotie", R.drawable.goy));
        allGames.add(new Game("Elden Ring NightReign", R.drawable.nightreign2));
        allGames.add(new Game("Phantom Blade Zero", R.drawable.pb0));
        allGames.add(new Game("Prince of Persia: The Lost Crown", R.drawable.pop));
        allGames.add(new Game("Onimusha", R.drawable.onimusha));
        allGames.add(new Game("Tides of Annihilation", R.drawable.tides));

    }

    private void filterGames(String query) {
        filteredGames.clear();
        if (query.trim().isEmpty()) {
            categoryRecyclerView.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.GONE);
        } else {
            for (Game game : allGames) {
                if (game.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredGames.add(game);
                }
            }
            categoryRecyclerView.setVisibility(View.GONE);
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            categoryRecyclerView.setVisibility(View.GONE);
        }
        searchResultsAdapter.notifyDataSetChanged();
    }

    private void filterGamesByCategory(String category) {
        List<Game> categoryGames = new ArrayList<>();
        // Since Game class doesn't have categories, we'll simulate filtering based on game titles
        // Alternatively, define a mapping of categories to games
        if (category.equalsIgnoreCase("Action")) {
            categoryGames.add(new Game("Elden Ring", R.drawable.eldenring));
            categoryGames.add(new Game("Ghost of Yotie", R.drawable.goy));
            categoryGames.add(new Game("Elden Ring NightReign", R.drawable.nightreign2));
            categoryGames.add(new Game("Cyberpunk 2077", R.drawable.cyberpunk));
            categoryGames.add(new Game("God of War", R.drawable.god2));
        } else if (category.equalsIgnoreCase("Adventure")) {
            categoryGames.add(new Game("Days Gone", R.drawable.daysgone));
            categoryGames.add(new Game("Witcher 3", R.drawable.witcher3));
            categoryGames.add(new Game("Elden Ring NightReign", R.drawable.nightreign2));
            categoryGames.add(new Game("Red Dead Redemption 2", R.drawable.rdr2));
        } else if (category.equalsIgnoreCase("First person shooter")) {
            categoryGames.add(new Game("Call of Duty", R.drawable.ghost));
        } else if (category.equalsIgnoreCase("New on Steam")) {
            categoryGames.add(new Game("Hades", R.drawable.hades));
        } else if (category.equalsIgnoreCase("Single player")) {
            categoryGames.add(new Game("Horizon Zero Dawn", R.drawable.horizon));
            categoryGames.add(new Game("Phantom Blade Zero", R.drawable.pb0));
            categoryGames.add(new Game("God of War", R.drawable.god2));
        } else if (category.equalsIgnoreCase("Indie")) {
            categoryGames.add(new Game("Hades", R.drawable.hades));
            categoryGames.add(new Game("Prince of Persia: The Lost Crown", R.drawable.pop));
        } else {
            // For other categories, show a subset of games
            categoryGames.add(new Game("Elden Ring", R.drawable.eldenring));
            categoryGames.add(new Game("Hades", R.drawable.hades));
        }
        categoryGamesAdapter.updateGames(categoryGames);
        categoryRecyclerView.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        categoryGamesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGameClick(Game game) {
        Fragment fragment = GameDetailsFragment.newInstance(game.getTitle(), game.getImageResId());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}