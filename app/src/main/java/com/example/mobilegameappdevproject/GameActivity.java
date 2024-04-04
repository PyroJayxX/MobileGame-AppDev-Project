package com.example.mobilegameappdevproject;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public ConstraintLayout guideScreen;
    public ImageView hp_icon1;
    public ImageView hp_icon2;
    public ImageView hp_icon3;
    public TextView txtTimer;
    public TextView txtScore;
    public TextView txtHighScore;
    public ImageButton btnPause;
    public ImageButton btnVolume;
    public ProgressBar progressBar;
    public ViewSwitcher flippedCardA = null, flippedCardB = null;
    public ImageView card1, card2, card3, card4, card5,card6, card7, card8, card9, card10,
            card11, card12, card13, card14, card15, card16, card17, card18, card19, card20;
    boolean gameInProgress = false, isFlipping = false, isVictory = false, isBgmMuted;
    private boolean initRunning, initWasRunning, cdRunning, cdWasRunning;
    int cdSec = 0, flippedCardCount = 0, hazardCardCount = 0, flippedCardTemp, Score = 0, currentHighScore;
    private int health = 3, initSec = 4;
    String gameMode;
    SoundManager soundManager;
    SharedPreferences localHighScore;
    SharedPreferences.Editor localScoreEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        isBgmMuted = getIntent().getBooleanExtra("isBgmMuted", false);
        gameMode = getIntent().getStringExtra("Mode");
        if(gameMode!=null && gameMode.equals("novice")) {
            setContentView(R.layout.activity_game_novice);
        }
        if(gameMode!=null && gameMode.equals("veteran")) {
            setContentView(R.layout.activity_game_veteran);
        }
        if(gameMode!=null && gameMode.equals("master")) {
            setContentView(R.layout.activity_game_master);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        localHighScore = getSharedPreferences(gameMode + "HighScore", MODE_PRIVATE);
        localScoreEditor = localHighScore.edit();
        currentHighScore = retrieveLocalHighScore(gameMode);
        soundManager = SoundManager.getInstance(getApplicationContext());

        new Handler().postDelayed(() -> {
            initializeViews();
            if(!isBgmMuted) {
                soundManager.playBackgroundMusic(R.raw.red_barons_theme);
            }
            if(isBgmMuted) {
                btnVolume.setImageResource(R.drawable.icon_muted);
            }
            initTimer();
            loadingScreen.setVisibility(View.GONE);
            mainGameScreen.setVisibility(View.VISIBLE);
        }, 1500);

        if (savedInstanceState != null) {
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
        // Pauses the game activity
        soundManager.playSoundEffect(R.raw.sfx_button);
        cdOnPause();
        soundManager.pauseBackgroundMusic();
        mainGameScreen.setVisibility(View.GONE);
        pausedScreen.setVisibility(View.VISIBLE);
    }
    public void btnResume(View v){
        // Resumes the game activity
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        cdOnStart();
        soundManager.resumeBackgroundMusic();
        mainGameScreen.setVisibility(View.VISIBLE);
        pausedScreen.setVisibility(View.GONE);
    }
    public void btnBack(View v) {
        // Returns to menu
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("isBgmMuted", isBgmMuted);
        soundManager.stopBackgroundMusic();
        resetVariables();
        startActivity(i);
    }
    public void btnRetry(View v){
        // Stops the activity and reopens it
        finish();
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        isVictory = false;
        flippedCardCount = 0;
        flippedCardA = null;
        flippedCardB = null;
        soundManager.stopBackgroundMusic();
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("Mode", gameMode);
        startActivity(i);
    }

    public void btnVolume(View v){
        // Mute and un-mute toggle of Bgm
        soundManager.playSoundEffect(R.raw.sfx_button);
        if (isBgmMuted) {
            btnVolume.setImageResource(R.drawable.icon_volume);
            if(SoundManager.backgroundMusicPlayer == null){
                soundManager.playBackgroundMusic(R.raw.red_barons_theme);
                return;
            }
            soundManager.resumeBackgroundMusic();
            isBgmMuted = false;
        } else {
            btnVolume.setImageResource(R.drawable.icon_muted);
            soundManager.pauseBackgroundMusic();
            isBgmMuted = true;
        }
    }

    public void btnGuide(View v){
        // Toggles How to Play screen visibility
        soundManager.playSoundEffect(R.raw.sfx_button);
        pausedScreen.setVisibility(View.GONE);
        guideScreen.setVisibility(View.VISIBLE);
    }

    public void exitGuide(View v){
        // Exits How to Play screen visibility
        soundManager.playSoundEffect(R.raw.sfx_btnexit);
        pausedScreen.setVisibility(View.VISIBLE);
        guideScreen.setVisibility(View.GONE);
    }

    public void damage(){
        // Reduces health by one point
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
        // Toggles countdown running boolean to true
        cdRunning = true;
    }
    void cdOnPause(){
        // Toggles countdown running boolean to false
        cdRunning = false;
    }
    private void initTimer() {
        // Runs the initial timer from 3 to 1
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
        // Runs the countdown timer of 100 seconds
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
    public int calculateScore(int remainingTimeSeconds, int hazardCardsPulled) {
        // Calculates score
        int score = (remainingTimeSeconds * 20) - (hazardCardsPulled*33);
        if (score>100) {
            return score;
        } else {
            return 75;
        }
    }

    private void initializeViews(){
        // Injects the elements from xml file to this java activity-controller
        // Views
        hp_icon1 = findViewById(R.id.hp1);
        hp_icon2 = findViewById(R.id.hp2);
        hp_icon3 = findViewById(R.id.hp3);
        progressBar = findViewById(R.id.progressBar);
        txtTimer = findViewById(R.id.txtTimer);
        txtScore = findViewById(R.id.txtScore);
        txtHighScore = findViewById(R.id.txtHighScore);

        // GIFS
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon1);
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon2);
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon3);


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
        card16 = findViewById(R.id.frontCardView16);
        card17 = findViewById(R.id.frontCardView17);
        card18 = findViewById(R.id.frontCardView18);
        card19 = findViewById(R.id.frontCardView19);
        card20 = findViewById(R.id.frontCardView20);
        CardUtils.shuffleCards(this);

        //Layouts
        loadingScreen = findViewById(R.id.loadingScreen);
        mainGameScreen = findViewById(R.id.mainGameScreen);
        pausedScreen = findViewById(R.id.pausedScreen);
        defeatScreen = findViewById(R.id.defeatScreen);
        victoryScreen = findViewById(R.id.victoryScreen);
        guideScreen = findViewById(R.id.guideScreen);

        //Buttons
        btnPause = findViewById(R.id.btnPause);
        btnVolume = findViewById(R.id.icon_volume);
    }

    // Card functions
    public void onCardClick(View v) {
        // Setters for flipped card variables
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
        // Gets the ImageSwitcher instance of selected card
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
            case 16: return card16;
            case 17: return card17;
            case 18: return card18;
            case 19: return card19;
            case 20: return card20;
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
        card16 = null;
        card17 = null;
        card18 = null;
        card19 = null;
        card20 = null;
        gameInProgress = false;
        isFlipping = false;
        isVictory = false;
        initRunning = false;
        initWasRunning = false;
        cdRunning = false;
        cdWasRunning = false;
        gameMode = null;
        initSec = 0;
        cdSec = 0;
        health = 0;
        flippedCardCount = 0;
        hazardCardCount = 0;
        flippedCardTemp = 0;
        Score = 0;
    }

    public void updateLocalHighScore(String gameMode, int newHighScore) {
        // Updates highscore in the local save
        SharedPreferences localHighScore = getSharedPreferences(gameMode + "HighScore", MODE_PRIVATE);
        SharedPreferences.Editor localScoreEditor = localHighScore.edit();
        localScoreEditor.putInt("highScore", newHighScore);
        localScoreEditor.apply();
    }

    public int retrieveLocalHighScore(String gameMode) {
        // Retrieves highscore from the local save
        SharedPreferences localHighScore = getSharedPreferences(gameMode + "HighScore", MODE_PRIVATE);
        return localHighScore.getInt("highScore", 0);
    }
}
