package com.example.pixelcodex;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private Switch themeSwitch;
    private Switch notificationsSwitch;
//    private Button clearWishlistButton;
    private TextView appVersionText;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize views
        themeSwitch = view.findViewById(R.id.themeSwitch);
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch);
//        clearWishlistButton = view.findViewById(R.id.clearWishlistButton);
        appVersionText = view.findViewById(R.id.appVersionText);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", 0);

        // Load saved preferences
        loadPreferences();

        // Set up listeners
        setupListeners();

        // Display app version
        displayAppVersion();

        return view;
    }

    private void loadPreferences() {
        // Load theme preference
        boolean isDarkTheme = sharedPreferences.getBoolean("darkTheme", true); // Default to dark theme
        themeSwitch.setChecked(isDarkTheme);
        AppCompatDelegate.setDefaultNightMode(isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Load notifications preference
        boolean notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true);
        notificationsSwitch.setChecked(notificationsEnabled);
    }

    private void setupListeners() {
        // Theme switch listener
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("darkTheme", isChecked);
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Notifications switch listener
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", isChecked);
            editor.apply();
            Toast.makeText(requireContext(), "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
            // TODO: Enable/disable notifications (e.g., using Firebase Cloud Messaging or local notifications)
        });

    }

    private void displayAppVersion() {
        try {
            PackageInfo packageInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            String version = packageInfo.versionName;
            appVersionText.setText("App Version: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appVersionText.setText("App Version: Unknown");
        }
    }
}