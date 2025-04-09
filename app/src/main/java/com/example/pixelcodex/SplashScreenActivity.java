package com.example.pixelcodex;

import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView logoImage;
    private LottieAnimationView lottieAnimationView;
    private TextView fadeInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_splash);

        // Initialize UI elements
        logoImage = findViewById(R.id.logoImage);
        lottieAnimationView = findViewById(R.id.loaderAnimation);
        fadeInText = findViewById(R.id.fadeInText); // Ensure this ID is in XML

        // Start animations
        lottieAnimationView.playAnimation();
        animateLogo();
        animateText(); // Start text animation

        // Transition to MainActivity after all animations complete
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 7000);
    }

    // Method to animate the logo (scale up and fade in)
    private void animateLogo() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoImage.startAnimation(fadeIn);
    }

    // Method to animate text with slide-in from left effect
    private void animateText() {
        String[] phrases = {
                "Your favourite games",
                "New releases",
                "Trailers",
                "and Gameplays",
                "in a single App"
        };

        Handler handler = new Handler();

        for (int i = 0; i < phrases.length; i++) {
            final int index = i;
            handler.postDelayed(() -> {
                fadeInText.setText(phrases[index]);

                // Apply smooth slide-in + fade-in animation
                Animation slideFadeIn = AnimationUtils.loadAnimation(this, R.anim.slide_fade_in);
                fadeInText.startAnimation(slideFadeIn);
            }, i * 1200); // Adjust delay for better pacing
        }
        Typeface customFont = ResourcesCompat.getFont(this, R.font.custom_font);
        fadeInText.setTypeface(customFont);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (lottieAnimationView != null) {
            lottieAnimationView.pauseAnimation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lottieAnimationView != null) {
            lottieAnimationView.playAnimation();
        }
    }
}
