package com.example.pixelcodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiChatFragment extends DialogFragment {

    private RecyclerView chatRecyclerView;
    private TextInputEditText promptEditText;
    private TextView greetingText;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;

    // Helper method to strip Markdown formatting
    private String stripMarkdown(String text) {
        if (text == null) return "Error: No response";
        // Remove common Markdown symbols: **, *, _, #, etc.
        return text.replaceAll("[*]{1,2}|[_#]+", "").trim();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gemini_chat, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        promptEditText = view.findViewById(R.id.promptEditText);
        greetingText = view.findViewById(R.id.greetingText);
        ImageButton sendButton = view.findViewById(R.id.sendButton);

        // Set up RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        // Initialize Gemini Model
        GenerativeModel generativeModel = new GenerativeModel(
                "gemini-2.0-flash",
                getString(R.string.gemini_api_key)
        );
        GenerativeModelFutures generativeModelFutures = GenerativeModelFutures.from(generativeModel);

        // Send Button Listener
        sendButton.setOnClickListener(v -> {
            String prompt = promptEditText.getText().toString().trim();
            if (!prompt.isEmpty()) {
                // Hide greeting and show RecyclerView on first message
                if (chatMessages.isEmpty()) {
                    greetingText.setVisibility(View.GONE);
                    chatRecyclerView.setVisibility(View.VISIBLE);
                }

                chatMessages.add(new ChatMessage(prompt, true));
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                promptEditText.setText("");

                // Call Gemini API
                Content content = new Content.Builder()
                        .addText(prompt)
                        .build();
                ListenableFuture<GenerateContentResponse> future = generativeModelFutures.generateContent(content);

                Executor executor = Executors.newSingleThreadExecutor();
                Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String responseText = stripMarkdown(result.getText());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                chatMessages.add(new ChatMessage(responseText, false));
                                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                            });
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                chatMessages.add(new ChatMessage("Error: " + t.getMessage(), false));
                                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                            });
                        }
                    }
                }, executor);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}