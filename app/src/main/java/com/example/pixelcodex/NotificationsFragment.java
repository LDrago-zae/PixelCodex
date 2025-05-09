package com.example.pixelcodex;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<NotificationItem> notifications;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        if (recyclerView == null) {
            Log.e("NotificationsFragment", "RecyclerView is null - check fragment_notifications.xml");
            Toast.makeText(getContext(), "Error: RecyclerView not found", Toast.LENGTH_LONG).show();
            return view;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notifications = new ArrayList<>();
        adapter = new NotificationsAdapter(getContext(), notifications);
        recyclerView.setAdapter(adapter);

        // Fetch notifications from Firestore with real-time updates
        fetchNotifications();

        return view;
    }

    private void fetchNotifications() {
        db.collection("announcements")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("NotificationsFragment", "Failed to fetch notifications: " + error.getMessage());
                            Toast.makeText(getContext(), "Error fetching notifications: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (value != null) {
                            notifications.clear();
                            if (value.isEmpty()) {
                                Log.w("NotificationsFragment", "No documents found in announcements collection");
                                Toast.makeText(getContext(), "No notifications available", Toast.LENGTH_SHORT).show();
                            } else {
                                for (QueryDocumentSnapshot doc : value) {
                                    try {
                                        NotificationItem notification = doc.toObject(NotificationItem.class);
                                        if (notification.getDescription() != null && notification.getEmail() != null && notification.getTimestamp() != null) {
                                            notifications.add(notification);
                                            Log.d("NotificationsFragment", "Added notification: " + notification.getDescription());
                                        } else {
                                            Log.w("NotificationsFragment", "Invalid notification data in doc: " + doc.getId());
                                        }
                                    } catch (Exception e) {
                                        Log.w("NotificationsFragment", "Failed to deserialize document: " + doc.getId() + ", error: " + e.getMessage());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                Log.d("NotificationsFragment", "Total notifications loaded: " + notifications.size());
                            }
                        } else {
                            Log.w("NotificationsFragment", "QuerySnapshot is null");
                        }
                    }
                });
    }
}