package com.example.pixelcodex;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DashboardAdapter {
    public static class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
        private static final String TAG = "GameAdapter";
        private final List<GameItem> games;

        public GameAdapter(List<GameItem> games) {
            this.games = games;
        }

        @NonNull
        @Override
        public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
            return new GameViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
            GameItem game = games.get(position);
            Log.d(TAG, "Game at position " + position + ": title=" + game.getTitle());
            holder.gameName.setText(game.getTitle());
        }

        @Override
        public int getItemCount() {
            return games.size();
        }

        public static class GameViewHolder extends RecyclerView.ViewHolder {
            TextView gameName;

            public GameViewHolder(@NonNull View itemView) {
                super(itemView);
                gameName = itemView.findViewById(R.id.gameName);
            }
        }
    }

    public static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private static final String TAG = "UserAdapter";
        private final List<UserItem> users;

        public UserAdapter(List<UserItem> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            UserItem user = users.get(position);
            Log.d(TAG, "User at position " + position + ": name=" + user.getName());
            holder.userName.setText(user.getName() != null ? user.getName() : "Unknown");
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView userName;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.userName);
            }
        }
    }
}