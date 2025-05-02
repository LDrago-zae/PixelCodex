package com.example.pixelcodex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (message.isFromUser()) {
            holder.userMessageText.setText(message.getText());
            holder.userMessageText.setVisibility(View.VISIBLE);
            holder.geminiMessageText.setVisibility(View.GONE);
        } else {
            holder.geminiMessageText.setText(message.getText());
            holder.geminiMessageText.setVisibility(View.VISIBLE);
            holder.userMessageText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView geminiMessageText;
        TextView userMessageText;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            geminiMessageText = itemView.findViewById(R.id.geminiMessageText);
            userMessageText = itemView.findViewById(R.id.userMessageText);
        }
    }
}