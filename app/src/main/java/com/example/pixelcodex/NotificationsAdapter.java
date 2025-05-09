package com.example.pixelcodex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificationItem> notifications;

    public NotificationsAdapter(Context context, List<NotificationItem> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);
        holder.gameDescription.setText(notification.getDescription());
        holder.gameEmail.setText("Email: " + notification.getEmail());
        holder.gameTimestamp.setText(notification.getTimestampString());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gameDescription, gameEmail, gameTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameDescription = itemView.findViewById(R.id.game_description);
            gameEmail = itemView.findViewById(R.id.game_email);
            gameTimestamp = itemView.findViewById(R.id.game_timestamp);
        }
    }
}