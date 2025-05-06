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
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final Context context;
    private final List<String> categories;
    private OnCategoryClickListener onCategoryClickListener;

    // Interface for click events
    public interface OnCategoryClickListener {
        void onCategoryClick(int position, String categoryName);
    }

    public CategoryAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
    }

    // Setter for click listener
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String categoryName = categories.get(position);
        holder.categoryText.setText(categoryName);

        // Apply fade-in animation with staggered delay
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition != RecyclerView.NO_POSITION) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
            animation.setStartOffset(adapterPosition * 150L); // Delay for sequential effect
            holder.itemView.startAnimation(animation);
        }

        // Set category image dynamically
        int imageResource = getCategoryImage(categoryName);
        holder.categoryImage.setImageResource(imageResource);

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(position, categoryName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        ImageView categoryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
            categoryImage = itemView.findViewById(R.id.categoryImage);
        }
    }

    // Helper method to return images based on category
    private int getCategoryImage(String category) {
        category = category.toLowerCase();
        switch (category) {
            case "adventure":
                return R.drawable.goy;
            case "action":
                return R.drawable.nightreign2;
            case "casual":
                return R.drawable.ffvii;
            case "first person shooter":
                return R.drawable.battlefield;
            case "single player":
                return R.drawable.god3;
            case "multiplayer":
                return R.drawable.mr;
            case "indie":
                return R.drawable.pop;
            default:
                return R.drawable.nightreign; // Default fallback image

        }
    }
}