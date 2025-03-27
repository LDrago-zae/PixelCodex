package com.example.pixelcodex;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize RecyclerView for options (Account Info, Edit Profile, etc.)
        RecyclerView optionsRecyclerView = view.findViewById(R.id.optionsRecyclerView);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load options data
        List<ProfileOption> options = new ArrayList<>();
        options.add(new ProfileOption(R.drawable.profile2, "Account Info"));
        options.add(new ProfileOption(R.drawable.edit, "Edit Profile"));
        options.add(new ProfileOption(R.drawable.gift2, "Gifts"));
        options.add(new ProfileOption(R.drawable.wishlist2, "Manage Wishlist"));

        // Set adapter
        ProfileOptionAdapter adapter = new ProfileOptionAdapter(getContext(), options);
        optionsRecyclerView.setAdapter(adapter);

        return view;
    }
}
