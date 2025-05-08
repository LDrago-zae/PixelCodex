package com.example.pixelcodex;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RawgApiService {
    @GET("games")
    Call<RawgGamesResponse> getRecentGames(
            @Query("key") String apiKey,
            @Query("dates") String dates,
            @Query("ordering") String ordering,
            @Query("page_size") int pageSize
    );

    @GET("games/{id}")
    Call<RawgGameDetail> getGameDetails(
            @Path("id") String id,
            @Query("key") String apiKey
    );
}

class RawgGamesResponse {
    private int count;
    private String next;
    private String previous;
    private List<RawgGame> results;

    public List<RawgGame> getResults() { return results; }
}

class RawgGame {
    private int id;
    private String name;
    private String background_image;
    private String released;
    private String updated;
    private Integer metacritic;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBackgroundImage() { return background_image; }
    public String getReleased() { return released; }
    public String getUpdated() { return updated; }
    public Integer getMetacritic() { return metacritic; }
}

class RawgGameDetail {
    private String description;

    public String getDescription() { return description; }
}