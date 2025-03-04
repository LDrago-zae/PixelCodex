package com.example.pixelcodex;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the Sign In button
        Button signInButton = findViewById(R.id.signInButton);

        // Set an OnClickListener to handle the Sign In button click
        signInButton.setOnClickListener(v -> {
            // Create an Intent to navigate to MainActivity3 (home screen)
            Intent intent = new Intent(MainActivity.this, MainActivity3.class);

            // Start MainActivity3
            startActivity(intent);
        });

        Button signUpButton = findViewById(R.id.signUpText);

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });
    }
}
