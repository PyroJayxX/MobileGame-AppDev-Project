package com.example.mobilegameappdevproject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

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
    public ConstraintLayout defeatScreen;
    public ConstraintLayout pausedScreen;
    public ConstraintLayout victoryScreen;
    public ImageView hp_icon1;
    public ImageView hp_icon2;
    public ImageView hp_icon3;
    public TextView txtTimer;
    public TextView txtScore;
    public ImageButton btnPause;
    public ProgressBar progressBar;
    public ViewSwitcher flippedCardA = null, flippedCardB = null;
    public ImageView card1, card2, card3, card4, card5,card6, card7,
            card8, card9, card10, card11, card12, card13, card14, card15;
    boolean gameInProgress = false, isFlipping = false, isVictory = false, isMortal;
    private boolean initRunning, initWasRunning, cdRunning, cdWasRunning;
    int cdSec = 0, flippedCardCount = 0, hazardCardCount = 0, flippedCardTemp, Score = 0;
    private int health = 3, initSec = 4;
    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        isMortal = getIntent().getBooleanExtra("Mode", false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Encapsulation
        soundManager = SoundManager.getInstance(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                soundManager.playBackgroundMusic(R.raw.red_barons_theme);
                initializeViews();
                initTimer();
                loadingScreen.setVisibility(View.GONE);
                mainGameScreen.setVisibility(View.VISIBLE);
            }
        }, 1500);


        if (savedInstanceState != null){
            initSec = savedInstanceState.getInt("initSec");
            initRunning = savedInstanceState.getBoolean("initRunning");
            initWasRunning = savedInstanceState.getBoolean("initWasRunning");
            cdSec = savedInstanceState.getInt("cdSec");
            cdRunning = savedInstanceState.getBoolean("cdRunning");
            cdWasRunning = savedInstanceState.getBoolean("cdWasRunning");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetVariables();
        soundManager.stopBackgroundMusic();
    }
    @Override
    protected void onPause() {
        super.onPause();
        soundManager.pauseBackgroundMusic();
        initWasRunning = initRunning;
        initRunning = false;
        if(cdRunning){
            cdWasRunning = true;
            cdOnPause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        soundManager.resumeBackgroundMusic();

        if (initWasRunning){
            initRunning = true;
        }
        if (cdWasRunning){
            cdWasRunning = false;
            cdOnStart();
        }

    }

    // Buttons
    public void btnPause(View v){
        soundManager.playSoundEffect(R.raw.sfx_button);
        cdOnPause();
        soundManager.pauseBackgroundMusic();
        mainGameScreen.setVisibility(View.GONE);
        pausedScreen.setVisibility(View.VISIBLE);
    }
    public void btnResume(View v){
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        cdOnStart();
        soundManager.resumeBackgroundMusic();
        mainGameScreen.setVisibility(View.VISIBLE);
        pausedScreen.setVisibility(View.GONE);
    }
    public void btnBack(View v) {
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        soundManager.stopBackgroundMusic();
        resetVariables();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void btnRetry(View v){
        finish();
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        isVictory = false;
        flippedCardCount = 0;
        flippedCardA = null;
        flippedCardB = null;
        soundManager.stopBackgroundMusic();
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("Mode", isMortal);
        startActivity(i);
    }

    public void damage(){
        switch(health){
            case 3:
                health--;
                Glide.with(this).load(R.drawable.hp_icon_dmg).into(hp_icon3);
                break;
            case 2:
                    health--;
                    Glide.with(this).load(R.drawable.hp_icon_dmg).into(hp_icon2);
                    break;
            case 1:
                    health--;
                    Glide.with(this).load(R.drawable.hp_icon_dmg).into(hp_icon1);
                    soundManager.stopBackgroundMusic();
                    soundManager.playSoundEffect(R.raw.sfx_gameover);
                    soundManager.playBackgroundMusic(R.raw.bgm_gameover);
                    cdRunning = !cdRunning;
                    mainGameScreen.setVisibility(View.GONE);
                    defeatScreen.setVisibility(View.VISIBLE);
                    gameInProgress = false;
                break;

        }
    }

    // Timer Methods
    private void cdOnStart(){
        cdRunning = true;
    }
    void cdOnPause(){
        cdRunning = false;
    }
    private void initTimer() {
        initRunning = true;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (initRunning) {
                    txtTimer.setText(String.valueOf(initSec-1));
                    if (initSec==1){
                        txtTimer.setText(R.string.go_text);
                    } else if (initSec==0){
                        txtTimer.setVisibility(View.INVISIBLE);
                        btnPause.setVisibility(View.VISIBLE);
                        handler.removeCallbacksAndMessages(null);
                        initRunning = !initRunning;
                        gameInProgress = true;
                        cdTimer();
                    }
                    initSec--;
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
                    progressBar.setProgress(cdSec);
                    if (cdSec >= 100 && !isVictory) {
                        soundManager.stopBackgroundMusic();
                        soundManager.playSoundEffect(R.raw.sfx_gameover);
                        cdRunning = !cdRunning;
                        handler.removeCallbacksAndMessages(null);
                        mainGameScreen.setVisibility(View.GONE);
                        defeatScreen.setVisibility(View.VISIBLE);
                        gameInProgress = false;
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    // Initialization Methods
    public int calculateScore(int remainingTimeSeconds, int hazardCardsPulled) {
        int score = (remainingTimeSeconds * 20) - (hazardCardsPulled*33);
        if (score>100) {
            return score;
        } else {
            return 75;
        }
    }

    private void initializeViews(){
        // Views
        hp_icon1 = findViewById(R.id.hp1);
        hp_icon2 = findViewById(R.id.hp2);
        hp_icon3 = findViewById(R.id.hp3);
        progressBar = findViewById(R.id.progressBar);
        txtTimer = findViewById(R.id.txtTimer);
        txtScore = findViewById(R.id.txtScore);

        // GIFS
        if(isMortal) {
            Glide.with(this).load(R.drawable.hp_icon).into(hp_icon1);
            Glide.with(this).load(R.drawable.hp_icon).into(hp_icon2);
            Glide.with(this).load(R.drawable.hp_icon).into(hp_icon3);
        }
        if(!isMortal) {
            Glide.with(this).load(R.drawable.immortal_heart).into(hp_icon1);
            Glide.with(this).load(R.drawable.immortal_heart).into(hp_icon2);
            Glide.with(this).load(R.drawable.immortal_heart).into(hp_icon3);
        }

        //Card Objects
        card1 = findViewById(R.id.frontCardView1);
        card2 = findViewById(R.id.frontCardView2);
        card3 = findViewById(R.id.frontCardView3);
        card4 = findViewById(R.id.frontCardView4);
        card5 = findViewById(R.id.frontCardView5);
        card6 = findViewById(R.id.frontCardView6);
        card7 = findViewById(R.id.frontCardView7);
        card8 = findViewById(R.id.frontCardView8);
        card9 = findViewById(R.id.frontCardView9);
        card10 = findViewById(R.id.frontCardView10);
        card11 = findViewById(R.id.frontCardView11);
        card12 = findViewById(R.id.frontCardView12);
        card13 = findViewById(R.id.frontCardView13);
        card14 = findViewById(R.id.frontCardView14);
        card15 = findViewById(R.id.frontCardView15);
        CardUtils.shuffleCards(this);

        //Layouts
        loadingScreen = findViewById(R.id.loadingScreen);
        mainGameScreen = findViewById(R.id.mainGameScreen);
        pausedScreen = findViewById(R.id.pausedScreen);
        defeatScreen = findViewById(R.id.defeatScreen);
        victoryScreen = findViewById(R.id.victoryScreen);

        //Buttons
        btnPause = findViewById(R.id.btnPause);
    }

    // Card functions
    public void onCardClick(View v) {
        if (gameInProgress && !isFlipping) {
            ViewSwitcher card = (ViewSwitcher) v;
            if (flippedCardA == null && card != flippedCardB) {
                CardUtils.flipCard(this, card);
                flippedCardA = card;
            } else if (flippedCardB == null && card != flippedCardA) {
                CardUtils.flipCard(this, card);
                flippedCardB = card;
            } else if (flippedCardA != null && card == flippedCardA) {
                CardUtils.flipCard(this, card);
                flippedCardA = null;
            } else if (flippedCardB != null && card == flippedCardB) {
                CardUtils.flipCard(this, card);
                flippedCardA = null;
            }
        }
    }

    ImageView getCard(int index) {
        switch (index) {
            case 1: return card1;
            case 2: return card2;
            case 3: return card3;
            case 4: return card4;
            case 5: return card5;
            case 6: return card6;
            case 7: return card7;
            case 8: return card8;
            case 9: return card9;
            case 10: return card10;
            case 11: return card11;
            case 12: return card12;
            case 13: return card13;
            case 14: return card14;
            case 15: return card15;
            default: return null;
        }
    }

    private void resetVariables() {
        loadingScreen = null;
        mainGameScreen = null;
        defeatScreen = null;
        pausedScreen = null;
        victoryScreen = null;
        hp_icon1 = null;
        hp_icon2 = null;
        hp_icon3 = null;
        txtTimer = null;
        txtScore = null;
        btnPause = null;
        progressBar = null;
        flippedCardA = null;
        flippedCardB = null;
        card1 = null;
        card2 = null;
        card3 = null;
        card4 = null;
        card5 = null;
        card6 = null;
        card7 = null;
        card8 = null;
        card9 = null;
        card10 = null;
        card11 = null;
        card12 = null;
        card13 = null;
        card14 = null;
        card15 = null;
        gameInProgress = false;
        isFlipping = false;
        isVictory = false;
        initRunning = false;
        initWasRunning = false;
        cdRunning = false;
        cdWasRunning = false;
        isMortal = false;
        initSec = 0;
        cdSec = 0;
        health = 0;
        flippedCardCount = 0;
        hazardCardCount = 0;
        flippedCardTemp = 0;
        Score = 0;
    }
}
