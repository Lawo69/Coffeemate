package com.example.coffeemate2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class TearmConditonPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tearm_conditon_page);
        Objects.requireNonNull(getSupportActionBar()).hide(); //This line hide the Action bar
    }
}