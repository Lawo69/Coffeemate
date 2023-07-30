package com.example.coffeemate2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class LoadingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);
        Objects.requireNonNull(getSupportActionBar()).hide(); //This line hide the Action bar

        View mContentView = findViewById(R.id.lordBar);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        //loading bar
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(LoadingPage.this, LoginSignupPage.class));
            finish();
        },4000);


    }

}