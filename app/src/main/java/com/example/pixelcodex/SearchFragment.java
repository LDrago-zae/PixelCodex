package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize RecyclerView
        RecyclerView categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView);
        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchPlate.setBackground(null);
        setupSearchView();

        // Define categories
        List<String> categories = Arrays.asList(
                "New on Steam", "Adventure", "Action", "Casual", "First person shooter", "Single player", "Multiplayer", "Indie"
        );

        // Set adapter
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categories);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Dismiss keyboard when touching outside SearchView
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View focusedView = getActivity() != null ? getActivity().getCurrentFocus() : null;
                if (focusedView != null) {
                    focusedView.clearFocus();
                }
            }
            v.performClick(); // Ensure the touch event is properly handled
            return false;
        });

        return view;
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false); // Ensure it stays expanded
        searchView.setFocusable(false); // Prevent auto-focus on fragment load
        searchView.setFocusableInTouchMode(false);
        searchView.clearFocus(); // Clear any default focus

        // Ensure the keyboard appears only when tapped
        searchView.setOnClickListener(v -> {
            searchView.setFocusableInTouchMode(true);
            searchView.setFocusable(true);
            searchView.requestFocus();
        });
    }
}
