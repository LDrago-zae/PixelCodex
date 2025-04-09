package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WishlistFragment extends Fragment {

    private WishlistAdapter adapter;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.wishlistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        dbHelper = new DatabaseHelper(requireContext());

        // Load wishlist games
        List<Game> gameList = dbHelper.getWishlistGames();
        adapter = new WishlistAdapter(gameList);
        adapter.setDatabaseHelper(dbHelper); // Pass the DatabaseHelper to the adapter
        recyclerView.setAdapter(adapter);

        // Set up swipe-to-remove
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Game game = adapter.getItem(position);
                dbHelper.removeGameFromWishlist(game.getTitle());
                adapter.removeItem(position);
                Toast.makeText(requireContext(), game.getTitle() + " removed from wishlist", Toast.LENGTH_SHORT).show();
            }
        };
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView);

        return view;
    }
}