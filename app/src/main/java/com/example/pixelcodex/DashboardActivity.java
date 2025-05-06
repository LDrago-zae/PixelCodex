package com.example.pixelcodex;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private TextView gamesCountTextView, usersCountTextView, requestsCountTextView;
    private DashboardAdapter.GameAdapter gameAdapter;
    private DashboardAdapter.UserAdapter userAdapter;
    private final List<GameItem> gameList = new ArrayList<>();
    private final List<UserItem> userList = new ArrayList<>();

    private FirebaseFirestore firestore;
    private DatabaseReference gamesRef, requestsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI elements
        gamesCountTextView = findViewById(R.id.gamesCount);
        usersCountTextView = findViewById(R.id.usersCount);
        requestsCountTextView = findViewById(R.id.revenueAmount);
        RecyclerView latestGamesRecyclerView = findViewById(R.id.latestGamesRecyclerView);
        RecyclerView recentUsersRecyclerView = findViewById(R.id.recentUsersRecyclerView);

        // Set layout managers for RecyclerViews
        latestGamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gamesRef = database.getReference("games");
        requestsRef = database.getReference("game_requests");

        // Initialize adapters
        gameAdapter = new DashboardAdapter.GameAdapter(gameList);
        userAdapter = new DashboardAdapter.UserAdapter(userList);
        latestGamesRecyclerView.setAdapter(gameAdapter);
        recentUsersRecyclerView.setAdapter(userAdapter);

        // Fetch data
        fetchGamesCount();
        fetchUsersCount();
        fetchRequestsCount();
        fetchLatestGames();
        fetchRecentUsers();
    }

    private void fetchGamesCount() {
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long totalGames = dataSnapshot.getChildrenCount();
                gamesCountTextView.setText(String.valueOf(totalGames));
                Log.d("DashboardActivity", "Total games: " + totalGames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DashboardActivity", "Failed to fetch games count: " + databaseError.getMessage());
                gamesCountTextView.setText("0");
            }
        });
    }

    private void fetchUsersCount() {
        firestore.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    long totalUsers = querySnapshot.size();
                    usersCountTextView.setText(String.valueOf(totalUsers));
                    Log.d("DashboardActivity", "Total users: " + totalUsers);
                })
                .addOnFailureListener(e -> {
                    Log.e("DashboardActivity", "Failed to fetch users count: " + e.getMessage());
                    usersCountTextView.setText("0");
                });
    }

    private void fetchRequestsCount() {
        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long totalRequests = dataSnapshot.getChildrenCount();
                requestsCountTextView.setText(String.valueOf(totalRequests));
                Log.d("DashboardActivity", "Total requests: " + totalRequests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DashboardActivity", "Failed to fetch requests count: " + databaseError.getMessage());
                requestsCountTextView.setText("0");
            }
        });
    }

    private void fetchLatestGames() {
        gamesRef.limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gameList.clear();
                Log.d("DashboardActivity", "Games snapshot: " + dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GameItem game = snapshot.getValue(GameItem.class);
                    if (game == null) {
                        Log.e("DashboardActivity", "Failed to deserialize game: " + snapshot);
                    } else {
                        Log.d("DashboardActivity", "Deserialized game: title=" + game.getTitle());
                        gameList.add(game);
                    }
                }
                gameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DashboardActivity", "Failed to fetch latest games: " + databaseError.getMessage());
            }
        });
    }

    private void fetchRecentUsers() {
        firestore.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    userList.clear();
                    Log.d("DashboardActivity", "Users snapshot size: " + querySnapshot.size());
                    for (var doc : querySnapshot) {
                        UserItem user = doc.toObject(UserItem.class);
                        if (user == null) {
                            Log.e("DashboardActivity", "Failed to deserialize user: " + doc.getData());
                        } else {
                            Log.d("DashboardActivity", "Deserialized user: name=" + user.getName());
                            userList.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("DashboardActivity", "Failed to fetch recent users: " + e.getMessage()));
    }
}