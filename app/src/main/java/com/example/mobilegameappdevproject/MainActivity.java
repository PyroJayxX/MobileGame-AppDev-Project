package com.example.mobilegameappdevproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity {

    private FrameLayout menuLayout;
    private FrameLayout mainGameLayout;
    public ImageView hp_icon1;
    public ImageView hp_icon2;
    public ImageView hp_icon3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialize
        menuLayout = findViewById(R.id.MenuLayout);
        mainGameLayout = findViewById(R.id.MainGameLayout);
        hp_icon1 = findViewById(R.id.hp1);
        hp_icon2 = findViewById(R.id.hp2);
        hp_icon3 = findViewById(R.id.hp3);
    }

    // MENU Methods
    public void btnStart(View v){
        mainGameLayout.setVisibility(View.VISIBLE);
        menuLayout.setVisibility(View.GONE);

        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon1);
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon2);
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon3);
    }

    // MAIN GAME Methods
    public void btnBack(View v){
        mainGameLayout.setVisibility(View.GONE);
        menuLayout.setVisibility(View.VISIBLE);
    }



}