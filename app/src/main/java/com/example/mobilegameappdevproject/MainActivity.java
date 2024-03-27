package com.example.mobilegameappdevproject;

import android.content.Intent;
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
    public ConstraintLayout menuScreen;
    public ConstraintLayout creditScreen;
    public ConstraintLayout modeScreen;
    public ImageButton btnStart;
    public ImageButton btnCredits;
    public ImageButton btnExit;
    private boolean isMortal;

    SoundManager soundManager;
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
        // Encapsulation
        soundManager = SoundManager.getInstance(getApplicationContext());

        initializeViews();
        soundManager.playBackgroundMusic(R.raw.tavern_loop);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.stopBackgroundMusic();
    }
    @Override
    protected void onPause() {
        super.onPause();
        soundManager.pauseBackgroundMusic();
    }
    @Override
    protected void onResume() {
        super.onResume();
        soundManager.resumeBackgroundMusic();
    }

    // MENU Methods
    public void btnStart(View v){
        soundManager.playSoundEffect(R.raw.sfx_button);
        modeScreen.setVisibility(View.VISIBLE);
        btnStart.setClickable(false);
        btnExit.setClickable(false);
        btnCredits.setClickable(false);
    }


    public void btnCredits(View v){
        soundManager.playSoundEffect(R.raw.sfx_button);
        menuScreen.setVisibility(View.GONE);
        creditScreen.setVisibility(View.VISIBLE);
    }

    public void btnHome(View v){
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        btnStart.setClickable(true);
        btnExit.setClickable(true);
        btnCredits.setClickable(true);
        menuScreen.setVisibility(View.VISIBLE);
        creditScreen.setVisibility(View.GONE);
        modeScreen.setVisibility(View.GONE);
    }

    public void btnQuit(View v){
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        soundManager.stopBackgroundMusic();
        finishAffinity();
    }

    public void btnMortal(View v){
        soundManager.playSoundEffect(R.raw.sfx_button);
        isMortal = true;
        soundManager.stopBackgroundMusic();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", isMortal);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void btnImmortal(View v){
        soundManager.playSoundEffect(R.raw.sfx_button);
        isMortal = false;
        soundManager.stopBackgroundMusic();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", isMortal);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
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