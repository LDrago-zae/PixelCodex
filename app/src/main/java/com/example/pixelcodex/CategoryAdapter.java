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
    private Context context;
    private List<String> categories;

    public CategoryAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
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
        if (category.equals("adventure")) {
            return R.drawable.goy;
        } else if (category.equals("action")) {
            return R.drawable.nightreign2;
        } else if (category.equals("casual")) {
            return R.drawable.ffvii;
        } else if (category.equals("first person shooter")) {
            return R.drawable.battlefield;
        } else if (category.equals("single player")) {
            return R.drawable.god3;
        } else if (category.equals("multiplayer")) {
            return R.drawable.mr;
        } else if (category.equals("indie")) {
            return R.drawable.pop;
        } else {
            return R.drawable.nightreign; // Default fallback image
        }
    }
}
