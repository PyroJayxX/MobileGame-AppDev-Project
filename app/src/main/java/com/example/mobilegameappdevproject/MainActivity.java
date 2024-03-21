package com.example.mobilegameappdevproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer menuBGM;
    public ConstraintLayout menuScreen;
    public ConstraintLayout creditScreen;
    public ConstraintLayout modeScreen;
    public ImageButton btnStart;
    public ImageButton btnCredits;
    public ImageButton btnExit;
    private boolean isMortal;

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
        playSoundEffect(R.raw.sfx_button);
        modeScreen.setVisibility(View.VISIBLE);
        btnStart.setClickable(false);
        btnExit.setClickable(false);
        btnCredits.setClickable(false);
    }


    public void btnCredits(View v){
        playSoundEffect(R.raw.sfx_button);
        menuScreen.setVisibility(View.GONE);
        creditScreen.setVisibility(View.VISIBLE);
    }

    public void btnHome(View v){
        playSoundEffect(R.raw.sfx_btnexit);
        btnStart.setClickable(true);
        btnExit.setClickable(true);
        btnCredits.setClickable(true);
        menuScreen.setVisibility(View.VISIBLE);
        creditScreen.setVisibility(View.GONE);
        modeScreen.setVisibility(View.GONE);
    }

    public void btnQuit(View v){
        playSoundEffect(R.raw.sfx_btnexit);
        if (menuBGM != null) {
            menuBGM.stop();
            menuBGM.release();
            menuBGM = null;
        }
        finishAffinity();
    }

    public void btnMortal(View v){
        playSoundEffect(R.raw.sfx_button);
        isMortal = true;
        if (menuBGM != null) {
            menuBGM.stop();
            menuBGM.release();
        }
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", isMortal);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void btnImmortal(View v){
        playSoundEffect(R.raw.sfx_button);
        isMortal = false;
        if (menuBGM != null) {
            menuBGM.stop();
            menuBGM.release();
        }
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", isMortal);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // Initialization Methods

    private void startMusic(){
        menuBGM = MediaPlayer.create(MainActivity.this, R.raw.tavern_loop);
        menuBGM.setLooping(true);
        menuBGM.start();
    }
    private void playSoundEffect(int soundResourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
        mediaPlayer.start();
    }

    private void initializeViews(){
        menuScreen = findViewById(R.id.menuScreen);
        creditScreen = findViewById(R.id.creditScreen);
        modeScreen = findViewById(R.id.modeScreen);
        btnStart = findViewById(R.id.btnStart);
        btnCredits = findViewById(R.id.btnCredits);
        btnExit = findViewById(R.id.btnExit);
    }
}