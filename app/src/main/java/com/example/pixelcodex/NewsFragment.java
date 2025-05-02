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
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // Initialize views
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        RecyclerView newsRecyclerView = view.findViewById(R.id.newsRecyclerView);

        // Set up TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("NEWS"));
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Featured"));



        // Set up RecyclerView
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<NewsItem> newsItems = new ArrayList<>();
        // Add sample news item (replace placeholder_image with an actual image resource)
        newsItems.add(new NewsItem(
                R.drawable.pb0, // Assuming pb0 is a placeholder image for Phantom Blade Zero
                "Phantom Blade Zero",
                "The Crimson Hunt Begins",
                "Embrace the shadows. The Crimson Hunt event runs from April 8 through May 6. Join Soul in a deadly pursuit through the Phantom World to unlock exclusive kungfu-punk weapons and artifacts in this limited 20-tier Shadow Pass.",
                "Tuesday at 11:00 PM",
                152,
                42,
                true
        ));

        newsItems.add(new NewsItem(
                R.drawable.acs1, // Placeholder for Assassin's Creed Shadows
                "Assassin's Creed Shadows",
                "Shadows of Sengoku Unveiled",
                "Step into the chaos of 16th-century Japan. Shadows of Sengoku launches on February 14. Wield the blades of Naoe and Yasuke to uncover hidden truths and earn exclusive shinobi and samurai gear in this 15-tier Legacy Pass.",
                "Wednesday at 9:00 AM",
                189,
                53,
                false // Event is live since the game releases before April 4, 2025
        ));

        newsItems.add(new NewsItem(
                R.drawable.mhw, // Placeholder for Monster Hunter Wilds
                "Monster Hunter Wilds",
                "Forbidden Lands Expedition",
                "Hunt or be hunted. The Forbidden Lands Expedition begins February 28. Join the hunt in a vast, untamed world to track a missing party and unlock exclusive layered armor sets in this 25-tier Hunter’s Pass.",
                "Thursday at 2:00 PM",
                210,
                65,
                false // Event is live since the game releases before April 4, 2025
        ));

        newsItems.add(new NewsItem(
                R.drawable.nightreign2, // Placeholder for Elden Ring: Nightreign
                "Elden Ring: Nightreign",
                "Nightreign Trials Begin",
                "Face the darkness together. The Nightreign Trials launch at the end of May. Team up to conquer new bosses in a cooperative realm and earn exclusive tarnished relics in this 20-tier Nightreign Pass.",
                "Friday at 10:00 AM",
                175,
                48,
                true // Event is upcoming since it launches after April 4, 2025
        ));

        newsItems.add(new NewsItem(
                R.drawable.mgsd, // Placeholder for Metal Gear Solid Delta
                "Metal Gear Solid Delta: Snake Eater",
                "Operation Snake Eater Redux",
                "Infiltrate the jungle. Operation Snake Eater Redux launches on August 28. Step into Naked Snake’s boots to rewrite history and unlock exclusive stealth camo in this 10-tier Enigma Pass.",
                "Monday at 8:00 PM",
                162,
                39,
                true // Event is upcoming since it launches after April 4, 2025
        ));

        newsItems.add(new NewsItem(
                R.drawable.gta6, // Placeholder for GTA VI
                "Grand Theft Auto VI",
                "Vice City Reborn",
                "Return to the neon streets. Vice City Reborn arrives in late 2025. Join Lucia and her partner in crime to dominate the underworld and unlock exclusive vehicles in this 30-tier Crimewave Pass.",
                "Thursday at 5:00 PM",
                305,
                92,
                true // Event is upcoming since it launches after April 4, 2025
        ));

        newsItems.add(new NewsItem(
                R.drawable.fh5,
                "Forza Horizon 5",
                "Horizon Festival Returns",
                "The festival is back. The Horizon Festival returns on March 15" +
                        " with new cars and events. Join"+
                        " the festival to unlock exclusive vehicles and rewards in this 20-tier Festival Pass.",
                "Saturday at 3:00 PM",
                120,
                30,
                true // Event is upcoming since it launches after April 4, 2025
        ));
        // Add more news items as needed
        NewsAdapter adapter = new NewsAdapter(newsItems);
        newsRecyclerView.setAdapter(adapter);

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection (e.g., filter news items based on tab)
                Toast.makeText(getContext(), "Tab selected: " + tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }
}