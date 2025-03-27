package com.example.pixelcodex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProfileOptionAdapter extends RecyclerView.Adapter<ProfileOptionAdapter.ViewHolder> {

    private final List<ProfileOption> options;
    private final Context context;

    public ProfileOptionAdapter(Context context, List<ProfileOption> options) {
        this.context = context;
        this.options = options;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_option_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfileOption item = options.get(position);
        holder.icon.setImageResource(item.getIcon());
        holder.title.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.optionIcon);
            title = itemView.findViewById(R.id.optionTitle);
        }
    }
}
