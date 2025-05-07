package com.example.pixelcodex;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminRequestsFragment extends Fragment {
    private AdminRequestsAdapter gameRequestsAdapter;
    private SupportQueriesAdapter supportQueriesAdapter;
    private final List<RequestItem> requestList = new ArrayList<>();
    private final List<SupportQueryItem> supportQueryList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_requests, container, false);

        // Initialize RecyclerViews
        RecyclerView adminRequestsRecyclerView = view.findViewById(R.id.adminRequestsRecyclerView);
        RecyclerView supportQueriesRecyclerView = view.findViewById(R.id.supportQueriesRecyclerView);

        adminRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        supportQueriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters
        gameRequestsAdapter = new AdminRequestsAdapter(requestList);
        supportQueriesAdapter = new SupportQueriesAdapter(supportQueryList);
        adminRequestsRecyclerView.setAdapter(gameRequestsAdapter);
        supportQueriesRecyclerView.setAdapter(supportQueriesAdapter);

        // Fetch game requests from Realtime Database
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("game_requests");
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requestList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RequestItem request = snapshot.getValue(RequestItem.class);
                    if (request != null) {
                        request.setRequestId(snapshot.getKey());
                        requestList.add(request);
                    } else {
                        Log.e("AdminRequestsFragment", "Failed to deserialize request: " + snapshot);
                    }
                }
                gameRequestsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminRequestsFragment", "Failed to fetch game requests: " + databaseError.getMessage());
            }
        });

        // Initialize Firestore and fetch support queries
        firestore = FirebaseFirestore.getInstance();
        fetchSupportQueries();

        return view;
    }

    private void fetchSupportQueries() {
        firestore.collection("support_queries")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("AdminRequestsFragment", "Failed to fetch support queries: " + error.getMessage());
                        return;
                    }
                    supportQueryList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            SupportQueryItem query = doc.toObject(SupportQueryItem.class);
                            query.setId(doc.getId()); // Store document ID for deletion
                            supportQueryList.add(query);
                        }
                    }
                    supportQueriesAdapter.notifyDataSetChanged();
                });
    }
}

class AdminRequestsAdapter extends RecyclerView.Adapter<AdminRequestsAdapter.RequestViewHolder> {
    private final List<RequestItem> requests;

    AdminRequestsAdapter(List<RequestItem> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_requests, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RequestItem request = requests.get(position);
        holder.userName.setText(request.getTitle() != null ? request.getTitle() : "Unknown Title");
        holder.requestText.setText(request.getDescription() != null ? request.getDescription() : "No Description");
        String timestampStr = "N/A";
        if (request.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timestampStr = sdf.format(new Date(request.getTimestamp()));
        }
        holder.requestTimestamp.setText(timestampStr);
        holder.approveButton.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Request approved!", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference("game_requests").child(request.getRequestId()).removeValue();
        });
        holder.rejectButton.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Request rejected!", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference("game_requests").child(request.getRequestId()).removeValue();
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView userName, requestText, requestTimestamp;
        Button approveButton, rejectButton;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            requestText = itemView.findViewById(R.id.requestText);
            requestTimestamp = itemView.findViewById(R.id.request_timestamp);
            approveButton = itemView.findViewById(R.id.buttonApprove);
            rejectButton = itemView.findViewById(R.id.buttonReject);
        }
    }
}

class SupportQueriesAdapter extends RecyclerView.Adapter<SupportQueriesAdapter.SupportQueryViewHolder> {
    private final List<SupportQueryItem> queries;

    SupportQueriesAdapter(List<SupportQueryItem> queries) {
        this.queries = queries;
    }

    @NonNull
    @Override
    public SupportQueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_query_item_layout, parent, false);
        return new SupportQueryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupportQueryViewHolder holder, int position) {
        SupportQueryItem query = queries.get(position);
        holder.subjectText.setText(query.getSubject() != null ? query.getSubject() : "No Subject");
        holder.descriptionText.setText(query.getDescription() != null ? query.getDescription() : "No Description");
        holder.emailText.setText(query.getEmail() != null ? query.getEmail() : "No Email");
        holder.resolveButton.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Support query resolved!", Toast.LENGTH_SHORT).show();
            FirebaseFirestore.getInstance().collection("support_queries").document(query.getId()).delete();
        });
    }

    @Override
    public int getItemCount() {
        return queries.size();
    }

    static class SupportQueryViewHolder extends RecyclerView.ViewHolder {
        TextView subjectText, descriptionText, emailText, queryTimestamp;
        Button resolveButton;

        SupportQueryViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            emailText = itemView.findViewById(R.id.emailText);
            resolveButton = itemView.findViewById(R.id.buttonResolve);
        }
    }
}

class RequestItem {
    private String requestId;
    private String description;
    private String platform;
    private String title;
    private Long timestamp;

    public RequestItem() {}

    public RequestItem(String requestId, String description, String platform, String title, Long timestamp) {
        this.requestId = requestId;
        this.description = description;
        this.platform = platform;
        this.title = title;
        this.timestamp = timestamp;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}

class SupportQueryItem {
    private String id; // Add ID field for Firestore document reference
    private String description;
    private String email;
    private String subject;

    public SupportQueryItem() {}

    public SupportQueryItem(String description, String email, String subject) {
        this.description = description;
        this.email = email;
        this.subject = subject;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}