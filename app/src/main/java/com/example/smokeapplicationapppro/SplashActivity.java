package com.example.smokeapplicationapppro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize ImageView for the GIF
        ImageView gifImageView = findViewById(R.id.gifImageView);

        // Load GIF using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.shield)  // Your GIF resource
                .into(gifImageView);


        TextView tvGetStarted = findViewById(R.id.tvGetStarted);

        tvGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
