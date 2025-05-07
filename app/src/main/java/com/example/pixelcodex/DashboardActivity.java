package com.example.pixelcodex;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private View admin_dashboard;
    private View admin_fragment_container;
    private BottomNavigationView admin_bottom_nav;
    private FloatingActionButton fab_add_game;

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
        admin_dashboard = findViewById(R.id.admin_dashboard);
        admin_fragment_container = findViewById(R.id.admin_fragment_container);
        admin_bottom_nav = findViewById(R.id.admin_bottom_nav);
        fab_add_game = findViewById(R.id.fab_add_game);

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

        // Handle BottomNavigationView item clicks
        admin_bottom_nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                admin_dashboard.setVisibility(View.VISIBLE);
                admin_fragment_container.setVisibility(View.GONE);
                fab_add_game.setVisibility(View.VISIBLE);
            } else {
                admin_dashboard.setVisibility(View.GONE);
                admin_fragment_container.setVisibility(View.VISIBLE);
                fab_add_game.setVisibility(View.GONE);

                Fragment selectedFragment = getSelectedFragment(itemId);
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                } else {
                    Log.e("DashboardActivity", "Selected fragment is null for itemId: " + itemId);
                }
            }
            return true;
        });

        // Fetch data
        fetchGamesCount();
        fetchUsersCount();
        fetchRequestsCount();
        fetchLatestGames();
        fetchRecentUsers();

        // Ensure the admin_dashboard screen is visible on startup
        if (savedInstanceState == null) {
            admin_bottom_nav.setSelectedItemId(R.id.nav_dashboard);
            admin_dashboard.setVisibility(View.VISIBLE);
            admin_fragment_container.setVisibility(View.GONE);
            fab_add_game.setVisibility(View.VISIBLE);
        }

        // Set up back press handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                if (backStackCount > 0) {
                    getSupportFragmentManager().popBackStack();
                    if (backStackCount == 1) {
                        admin_dashboard.setVisibility(View.VISIBLE);
                        admin_fragment_container.setVisibility(View.GONE);
                        admin_bottom_nav.setSelectedItemId(R.id.nav_dashboard);
                        fab_add_game.setVisibility(View.VISIBLE);
                    }
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    private Fragment getSelectedFragment(int itemId) {
        Fragment selectedFragment = null;
        if (itemId == R.id.nav_requests) {
            selectedFragment = new AdminRequestsFragment();
        } else if (itemId == R.id.announcements) {
            selectedFragment = new FragmentAnnouncement();
        }
        return selectedFragment;
    }

    private void loadFragment(Fragment fragment) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.admin_fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            Log.e("DashboardActivity", "Fragment transaction failed: " + e.getMessage());
        }
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