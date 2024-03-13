package com.example.mobilegameappdevproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedInputStream;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer menuBGM;
    private ConstraintLayout menuScreen;
    private ConstraintLayout loadingScreen;
    private ConstraintLayout splashArt;

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

        initializeViews();
        startMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (menuBGM != null) {
            menuBGM.release();
            menuBGM = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (menuBGM != null && menuBGM.isPlaying()) {
            menuBGM.pause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (menuBGM != null && !menuBGM.isPlaying()) {
            menuBGM.start();
        }
    }

    // MENU Methods
    public void btnStart(View v){
        if (menuBGM != null) {
            menuBGM.stop();
            menuBGM.release();
        }
       // loadingScreen.setVisibility(View.VISIBLE);
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    public void btnQuit(View v){
        if (menuBGM != null) {
            menuBGM.stop();
            menuBGM.release();
            menuBGM = null;
        }
        finishAffinity();
    }

    // Initialization Methods

    private void startMusic(){
        menuBGM = MediaPlayer.create(MainActivity.this, R.raw.tavern_loop);
        menuBGM.setLooping(true);
        menuBGM.start();
    }

    private void initializeViews(){
        menuScreen = findViewById(R.id.menuScreen);
        loadingScreen = findViewById(R.id.loadingScreen);
        splashArt = findViewById(R.id.splashArt);
    }

}