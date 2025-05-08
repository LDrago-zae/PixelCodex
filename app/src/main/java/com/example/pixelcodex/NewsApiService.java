package com.example.pixelcodex;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("everything")
    Call<NewsApiResponse> getNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("pageSize") int pageSize
    );
}

class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<NewsArticle> articles;

    public List<NewsArticle> getArticles() { return articles; }
}

class NewsArticle {
    private String title;
    private String description;
    private String urlToImage;
    private String publishedAt;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrlToImage() { return urlToImage; }
    public String getPublishedAt() { return publishedAt; }
}