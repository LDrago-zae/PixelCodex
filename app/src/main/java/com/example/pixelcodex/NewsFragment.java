package com.example.pixelcodex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsFragment extends Fragment {

    private NewsAdapter adapter;
    private List<NewsItem> newsItems;
    private RawgApiService rawgApiService;
    private String RAWG_API_KEY; // Initialized from string resource
    private DatabaseHelper dbHelper;
    private static final int POST_LIMIT = 5;
    private static final long CACHE_DURATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // Initialize API key from string resource
        RAWG_API_KEY = getString(R.string.RAWG_API_KEY);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        RecyclerView newsRecyclerView = view.findViewById(R.id.newsRecyclerView);

        tabLayout.addTab(tabLayout.newTab().setText("NEWS"));
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Featured"));

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsItems = new ArrayList<>();
        adapter = new NewsAdapter(newsItems);
        newsRecyclerView.setAdapter(adapter);

        // Initialize Database
        dbHelper = new DatabaseHelper(getContext());

        // Initialize Retrofit
        Retrofit rawgRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.rawg.io/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        rawgApiService = rawgRetrofit.create(RawgApiService.class);

        // Load initial data
        loadNewsData();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabText = tab.getText().toString();
                switch (tabText) {
                    case "NEWS":
                        loadNewsData();
                        break;
                    case "Upcoming":
                        loadUpcomingGames();
                        break;
                    case "Featured":
                        loadFeaturedGames();
                        break;
                }
                Toast.makeText(getContext(), "Tab selected: " + tabText, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void loadNewsData() {
        // Check cache first
        List<NewsItem> cachedItems = dbHelper.getCachedNewsItems("NEWS");
        if (cachedItems != null && !cachedItems.isEmpty()) {
            newsItems.clear();
            newsItems.addAll(cachedItems.subList(0, Math.min(cachedItems.size(), POST_LIMIT)));
            adapter.notifyDataSetChanged();
            Log.d("NewsFragment", "Loaded NEWS from cache");
            return;
        }

        // Fetch from API if cache is empty or expired
        String dateRange = "2025-04-08,2025-05-08";
        rawgApiService.getRecentGames(RAWG_API_KEY, dateRange, "-updated", POST_LIMIT).enqueue(new Callback<RawgGamesResponse>() {
            @Override
            public void onResponse(Call<RawgGamesResponse> call, Response<RawgGamesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    newsItems.clear();
                    List<RawgGame> games = response.body().getResults();
                    for (RawgGame game : games) {
                        fetchGameDetails(game, "NEWS");
                    }
                } else {
                    Log.e("NewsFragment", "Failed to load news: " + response.message());
                    Toast.makeText(getContext(), "Failed to load news: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RawgGamesResponse> call, Throwable t) {
                Log.e("NewsFragment", "News load failed: " + t.getMessage());
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchGameDetails(RawgGame game, String category) {
        rawgApiService.getGameDetails(String.valueOf(game.getId()), RAWG_API_KEY).enqueue(new Callback<RawgGameDetail>() {
            @Override
            public void onResponse(Call<RawgGameDetail> call, Response<RawgGameDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String updatedAt = game.getUpdated();
                    String formattedTimestamp;
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE 'at' h:mm a", Locale.getDefault());
                        Date date = inputFormat.parse(updatedAt);
                        formattedTimestamp = outputFormat.format(date);
                    } catch (Exception e) {
                        formattedTimestamp = "TBA";
                        Log.e("NewsFragment", "Timestamp parse error: " + e.getMessage());
                    }

                    String description = response.body().getDescription() != null ? response.body().getDescription() : "No description available.";
                    description = description.replaceAll("<[^>]+>", "");

                    NewsItem newsItem = new NewsItem(
                            game.getBackgroundImage() != null ? game.getBackgroundImage() : "",
                            game.getName() != null ? game.getName() : "No Title",
                            "Recently Updated Game",
                            description,
                            formattedTimestamp,
                            (int) (Math.random() * 300),
                            (int) (Math.random() * 100),
                            false
                    );

                    newsItems.add(newsItem);
                    adapter.notifyDataSetChanged();
                    dbHelper.cacheNewsItem(newsItem, category); // Cache the item
                    Log.d("NewsFragment", "Game details fetched and cached for: " + game.getName());
                } else {
                    Log.e("NewsFragment", "Failed to fetch game details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RawgGameDetail> call, Throwable t) {
                Log.e("NewsFragment", "Game details fetch failed: " + t.getMessage());
            }
        });
    }

    private void loadUpcomingGames() {
        // Check cache first
        List<NewsItem> cachedItems = dbHelper.getCachedNewsItems("Upcoming");
        if (cachedItems != null && !cachedItems.isEmpty()) {
            newsItems.clear();
            newsItems.addAll(cachedItems.subList(0, Math.min(cachedItems.size(), POST_LIMIT)));
            adapter.notifyDataSetChanged();
            Log.d("NewsFragment", "Loaded Upcoming from cache");
            return;
        }

        // Fetch from API
        String dateAfter = "2025-05-08";
        rawgApiService.getRecentGames(RAWG_API_KEY, dateAfter + ",2025-12-31", "-released", POST_LIMIT).enqueue(new Callback<RawgGamesResponse>() {
            @Override
            public void onResponse(Call<RawgGamesResponse> call, Response<RawgGamesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    newsItems.clear();
                    List<RawgGame> games = response.body().getResults();
                    for (RawgGame game : games) {
                        String timestamp = game.getReleased() != null ?
                                new SimpleDateFormat("EEEE 'at' h:mm a", Locale.getDefault()).format(new Date()) :
                                "TBA";
                        NewsItem newsItem = new NewsItem(
                                game.getBackgroundImage() != null ? game.getBackgroundImage() : "",
                                game.getName() != null ? game.getName() : "No Title",
                                "Upcoming Release",
                                "Release date: " + (game.getReleased() != null ? game.getReleased() : "TBA"),
                                timestamp,
                                (int) (Math.random() * 300),
                                (int) (Math.random() * 100),
                                true
                        );
                        newsItems.add(newsItem);
                        dbHelper.cacheNewsItem(newsItem, "Upcoming"); // Cache the item
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("NewsFragment", "Upcoming games loaded and cached successfully");
                } else {
                    Log.e("NewsFragment", "Failed to load upcoming games: " + response.message());
                    Toast.makeText(getContext(), "Failed to load upcoming games: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RawgGamesResponse> call, Throwable t) {
                Log.e("NewsFragment", "Upcoming games load failed: " + t.getMessage());
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFeaturedGames() {
        // Check cache first
        List<NewsItem> cachedItems = dbHelper.getCachedNewsItems("Featured");
        if (cachedItems != null && !cachedItems.isEmpty()) {
            newsItems.clear();
            newsItems.addAll(cachedItems.subList(0, Math.min(cachedItems.size(), POST_LIMIT)));
            adapter.notifyDataSetChanged();
            Log.d("NewsFragment", "Loaded Featured from cache");
            return;
        }

        // Fetch from API
        rawgApiService.getRecentGames(RAWG_API_KEY, null, "-rating", POST_LIMIT).enqueue(new Callback<RawgGamesResponse>() {
            @Override
            public void onResponse(Call<RawgGamesResponse> call, Response<RawgGamesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    newsItems.clear();
                    List<RawgGame> games = response.body().getResults();
                    for (RawgGame game : games) {
                        NewsItem newsItem = new NewsItem(
                                game.getBackgroundImage() != null ? game.getBackgroundImage() : "",
                                game.getName() != null ? game.getName() : "No Title",
                                "Featured Game",
                                "Rating: " + (game.getMetacritic() != null ? game.getMetacritic() : "N/A"),
                                new SimpleDateFormat("EEEE 'at' h:mm a", Locale.getDefault()).format(new Date()),
                                (int) (Math.random() * 300),
                                (int) (Math.random() * 100),
                                false
                        );
                        newsItems.add(newsItem);
                        dbHelper.cacheNewsItem(newsItem, "Featured"); // Cache the item
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("NewsFragment", "Featured games loaded and cached successfully");
                } else {
                    Log.e("NewsFragment", "Failed to load featured games: " + response.message());
                    Toast.makeText(getContext(), "Failed to load featured games: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RawgGamesResponse> call, Throwable t) {
                Log.e("NewsFragment", "Featured games load failed: " + t.getMessage());
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // SQLite Database Helper
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "news_cache.db";
        private static final int DATABASE_VERSION = 1;
        private static final String TABLE_NAME = "news_items";
        private static final String COLUMN_ID = "_id";
        private static final String COLUMN_CATEGORY = "category";
        private static final String COLUMN_IMAGE_URL = "image_url";
        private static final String COLUMN_TITLE = "title";
        private static final String COLUMN_SUBTITLE = "subtitle";
        private static final String COLUMN_DESCRIPTION = "description";
        private static final String COLUMN_TIMESTAMP = "timestamp";
        private static final String COLUMN_LIKES = "likes";
        private static final String COLUMN_COMMENTS = "comments";
        private static final String COLUMN_IS_UPCOMING = "is_upcoming";
        private static final String COLUMN_CACHE_TIME = "cache_time";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_IMAGE_URL + " TEXT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_SUBTITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_TIMESTAMP + " TEXT, " +
                    COLUMN_LIKES + " INTEGER, " +
                    COLUMN_COMMENTS + " INTEGER, " +
                    COLUMN_IS_UPCOMING + " INTEGER, " +
                    COLUMN_CACHE_TIME + " INTEGER)";
            db.execSQL(createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public void cacheNewsItem(NewsItem item, String category) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY, category);
            values.put(COLUMN_IMAGE_URL, item.getImageUrl());
            values.put(COLUMN_TITLE, item.getTitle());
            values.put(COLUMN_SUBTITLE, item.getSubtitle());
            values.put(COLUMN_DESCRIPTION, item.getDescription());
            values.put(COLUMN_TIMESTAMP, item.getTimestamp());
            values.put(COLUMN_LIKES, item.getLikeCount());
            values.put(COLUMN_COMMENTS, item.getCommentCount());
            values.put(COLUMN_IS_UPCOMING, item.isComingSoon() ? 1 : 0);
            values.put(COLUMN_CACHE_TIME, System.currentTimeMillis());

            db.insert(TABLE_NAME, null, values);
            db.close();
        }

        public List<NewsItem> getCachedNewsItems(String category) {
            List<NewsItem> items = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            long currentTime = System.currentTimeMillis();

            Cursor cursor = db.query(TABLE_NAME,
                    null,
                    COLUMN_CATEGORY + " = ? AND " + COLUMN_CACHE_TIME + " > ?",
                    new String[]{category, String.valueOf(currentTime - CACHE_DURATION_MS)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    NewsItem item = new NewsItem(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBTITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_UPCOMING)) == 1
                    );
                    items.add(item);
                } while (cursor.moveToNext());
                cursor.close();
            }
            db.close();
            return items;
        }
    }
}