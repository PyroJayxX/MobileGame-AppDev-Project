package com.example.mobilegameappdevproject;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class GameActivity extends AppCompatActivity {
    public ConstraintLayout loadingScreen;
    public ConstraintLayout mainGameScreen;
    public ConstraintLayout pausedScreen;
    public ImageView hp_icon1;
    public ImageView hp_icon2;
    public ImageView hp_icon3;
    public TextView txtTimer;
    public MediaPlayer gameBGM;

    public ImageButton btnPause;
    public ProgressBar progressBar;
    public ViewSwitcher card1, card2, card3, card4, card5,card6, card7,
            card8, card9, card10, card11, card12, card13, card14, card15;
    private boolean isFront = false, initRunning, initWasRunning, cdRunning;
    private int initSec = 0, cdSec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startMusic();
        initializeViews();
        initTimer();

        if (savedInstanceState != null){
            initSec = savedInstanceState.getInt("initSec");
            initRunning = savedInstanceState.getBoolean("initRunning");
            initWasRunning = savedInstanceState.getBoolean("initWasRunning");
            cdSec = savedInstanceState.getInt("cdSec");
            cdRunning = savedInstanceState.getBoolean("cdRunning");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMusic();
    }
    @Override
    protected void onPause() {
        super.onPause();
        pauseMusic();
        initWasRunning = initRunning;
        initRunning = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (gameBGM != null && !gameBGM.isPlaying()) {
            gameBGM.start();
        }
        if (initWasRunning){
            initRunning = true;
        }
    }

    // Buttons
    public void btnPause(View v){
        cdOnPause();
        pauseMusic();
        mainGameScreen.setVisibility(View.GONE);
        pausedScreen.setVisibility(View.VISIBLE);
    }
    public void btnResume(View v){
        cdOnStart();
        gameBGM.start();
        mainGameScreen.setVisibility(View.VISIBLE);
        pausedScreen.setVisibility(View.GONE);
    }
    public void btnBack(View v) {
        releaseMusic();
        //loadingScreen.setVisibility(View.VISIBLE);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
    public void btnRetry(View v){
        releaseMusic();
        finish();
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    // Card Logic
    public void onCardClick(View v){
        flipCard((ViewSwitcher) v);
    }
    private void flipCard(final ViewSwitcher card) {
        // Apply flip animation
        Animation flip = AnimationUtils.loadAnimation(this, R.anim.flip);
        Animation midFlip = AnimationUtils.loadAnimation(this, R.anim.flip_middle);
        flip.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the midFlip animation after the first animation ends
                card.startAnimation(midFlip);
            }
            @Override
            public void onAnimationRepeat(Animation animation){
            }
        });
        midFlip.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (isFront) {
                    card.showNext(); // Switch to the back card
                } else {
                    card.showPrevious(); // Switch to the front card
                }
                isFront = !isFront; // Toggle the card state
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        card.startAnimation(flip);
    }

    // Timer Methods
    private void cdOnStart(){
        cdRunning = true;
    }
    private void cdOnPause(){
        cdRunning = false;
    }
    private void initTimer() {
        initRunning = true;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (initRunning) {
                    initSec++;
                    txtTimer.setText(String.valueOf(initSec));
                    if (initSec==4){
                        txtTimer.setText(R.string.go_text);
                    }
                    if (initSec>4){
                        txtTimer.setVisibility(View.INVISIBLE);
                        btnPause.setVisibility(View.VISIBLE);
                        handler.removeCallbacksAndMessages(null);
                        initRunning = !initRunning;
                        cdTimer();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
    private void cdTimer() {
        cdRunning = true;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (cdRunning) {
                    cdSec++;
                    progressBar.setProgress(cdSec*2);
                    if (cdSec == 100) {
                        cdRunning = !cdRunning;
                        handler.removeCallbacksAndMessages(null);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    // Initialization Methods
    private void startMusic(){
        gameBGM = MediaPlayer.create(GameActivity.this, R.raw.red_barons_theme);
        gameBGM.setLooping(true);
        gameBGM.start();
    }
    private void pauseMusic(){
        if (gameBGM != null && gameBGM.isPlaying()) {
            gameBGM.pause();
        }
    }
    private void releaseMusic(){
        if (gameBGM != null) {
            gameBGM.stop();
            gameBGM.release();
            gameBGM = null;
        }
    }
    private void initializeViews(){
        // Views
        hp_icon1 = findViewById(R.id.hp1);
        hp_icon2 = findViewById(R.id.hp2);
        hp_icon3 = findViewById(R.id.hp3);
        progressBar = findViewById(R.id.progressBar);
        txtTimer = findViewById(R.id.txtTimer);


        // GIFS
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon1);
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon2);
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon3);

        //Card Objects
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);
        card7 = findViewById(R.id.card7);
        card8 = findViewById(R.id.card8);
        card9 = findViewById(R.id.card9);
        card10 = findViewById(R.id.card10);
        card11 = findViewById(R.id.card11);
        card12 = findViewById(R.id.card12);
        card13 = findViewById(R.id.card13);
        card14 = findViewById(R.id.card14);
        card15 = findViewById(R.id.card15);

        //Layouts
        loadingScreen = findViewById(R.id.loadingScreen);
        mainGameScreen = findViewById(R.id.mainGameScreen);
        pausedScreen = findViewById(R.id.pausedScreen);

        //Buttons
        btnPause = findViewById(R.id.btnPause);
    }
}