package com.example.pixelcodex;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ViewPager2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager2);  // Set the main activity layout

        // Accessing the CardView and its views programmatically
        CardView gameCard = findViewById(R.id.cardView); // Assuming you have a card view ID

        ImageView gameImage = findViewById(R.id.gameImage); // Game image
        TextView gameTitle = findViewById(R.id.gameTitle);  // Game title
        TextView gamePrice = findViewById(R.id.gamePrice);  // Game price

        // Now you can modify these views programmatically
        gameTitle.setText("New Game Title");
        gamePrice.setText("$20");

        // For example, setting an image programmatically
        gameImage.setImageResource(R.drawable.eldenring);
        gameImage.setImageResource(R.drawable.cyberpunk);
        gameImage.setImageResource(R.drawable.ghost);
    }
}
