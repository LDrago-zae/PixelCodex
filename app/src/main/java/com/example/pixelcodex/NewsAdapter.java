package com.example.pixelcodex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<NewsItem> newsItems;
    private Context context;

    public NewsAdapter(List<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsItems.get(position);
        // Use Glide to load the image URL
        Glide.with(context)
                .load(newsItem.getImageUrl())
                .placeholder(R.drawable.pb0) // Fallback image while loading or if URL is invalid
                .into(holder.newsImage);
        holder.newsTitle.setText(newsItem.getTitle());
        holder.newsSubtitle.setText(newsItem.getSubtitle());
        holder.newsDescription.setText(newsItem.getDescription());
        holder.newsTimestamp.setText(newsItem.getTimestamp());
        holder.likeCount.setText(String.valueOf(newsItem.getLikeCount()));
        holder.commentCount.setText(String.valueOf(newsItem.getCommentCount()));
        holder.comingSoonLabel.setVisibility(newsItem.isComingSoon() ? View.VISIBLE : View.GONE);

        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition != RecyclerView.NO_POSITION) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
            animation.setStartOffset(adapterPosition * 150L);
            holder.itemView.startAnimation(animation);
        }
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;
        TextView newsTitle;
        TextView newsSubtitle;
        TextView newsDescription;
        TextView newsTimestamp;
        TextView likeCount;
        TextView commentCount;
        TextView comingSoonLabel;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsSubtitle = itemView.findViewById(R.id.news_subtitle);
            newsDescription = itemView.findViewById(R.id.news_description);
            newsTimestamp = itemView.findViewById(R.id.news_timestamp);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            comingSoonLabel = itemView.findViewById(R.id.coming_soon_label);
        }
    }
}