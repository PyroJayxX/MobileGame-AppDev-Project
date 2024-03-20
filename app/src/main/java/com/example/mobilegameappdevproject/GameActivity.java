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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    public ConstraintLayout loadingScreen;
    public ConstraintLayout mainGameScreen;
    public ConstraintLayout defeatScreen;
    public ConstraintLayout pausedScreen;
    public ImageView hp_icon1;
    public ImageView hp_icon2;
    public ImageView hp_icon3;
    public TextView txtTimer;
    public MediaPlayer gameBGM;
    public ImageButton btnPause;
    public ProgressBar progressBar;
    public ViewSwitcher flippedCardA = null, flippedCardB = null, hazardA = null, hazardB = null, hazardC = null;
    public ImageView card1, card2, card3, card4, card5,card6, card7,
            card8, card9, card10, card11, card12, card13, card14, card15;
    private boolean gameInProgress = false, isFlipping = false,
            cardAIsFront = false, cardBIsFront = false, initRunning, initWasRunning, cdRunning;
    private int initSec = 0, cdSec = 0, health = 3, flippedCardCount = 0;
    private int flippedCardAID , flippedCardBID, flippedCardTemp; // Card Resource ID

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMusic();
                initializeViews();
                shuffleCards();
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameInProgress = false;
        releaseMusic();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (gameBGM != null && gameBGM.isPlaying()) {
            pauseMusic();
        }
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
        if (gameBGM != null && gameBGM.isPlaying()) {
            pauseMusic();
        }
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
        gameInProgress = false;
        flippedCardCount = 0;
        flippedCardA = null;
        flippedCardB = null;
        //loadingScreen.setVisibility(View.VISIBLE);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
    public void btnRetry(View v){
        gameInProgress = false;
        flippedCardCount = 0;
        flippedCardA = null;
        flippedCardB = null;
        releaseMusic();
        finish();
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    // Card Logic
    public void onCardClick(View v){
        if (gameInProgress && !isFlipping) {
            ViewSwitcher card = (ViewSwitcher) v;
            if (flippedCardA == null && card != flippedCardB) {
                flipCard(card);
                flippedCardA = card;
                flippedCardCount++;
            } else if (flippedCardB == null && card != flippedCardA) {
                flipCard(card);
                flippedCardB = card;
                flippedCardCount++;
            } else if (flippedCardA != null && card == flippedCardA){
                flipCard(card);
                flippedCardA = null;
                flippedCardCount--;
            } else if (flippedCardB != null && card == flippedCardB){
                flipCard(card);
                flippedCardA = null;
                flippedCardCount--;
            }
        }
    }

    public void cardComparator(){
        //FOR JASON
        if (flippedCardA.getTag() != null && flippedCardA.getTag().equals(R.drawable.mimic) || flippedCardA.getTag() != null && flippedCardA.getTag().equals(R.drawable.bomber) || flippedCardA.getTag() != null && flippedCardA.getTag().equals(R.drawable.poison)) {
            flippedCardA = null;
        } else if (flippedCardB.getTag() != null && flippedCardB.getTag().equals(R.drawable.mimic) || flippedCardB.getTag() != null && flippedCardB.getTag().equals(R.drawable.bomber) || flippedCardB.getTag() != null && flippedCardB.getTag().equals(R.drawable.poison)) {
            flippedCardB = null;
        } else {
            flipCard(flippedCardA);
            flipCard(flippedCardB);
            flippedCardCount = 0;
            flippedCardA = null;
            flippedCardB = null;
        }
    }
    private void shuffleCards() {
        Integer[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5,
                R.drawable.image6, R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
                R.drawable.image5, R.drawable.image6, R.drawable.mimic, R.drawable.bomber, R.drawable.poison};

        List<Integer> imageIndex = new ArrayList<>(Arrays.asList(images));
        Collections.shuffle(imageIndex);

        for (int i = 1; i <=15; i++) {
            ImageView card = getCard(i);
            if(card!=null) {
                card.setImageResource(imageIndex.get(i-1));
                card.setTag(imageIndex.get(i-1));
            }
        }
    }
    private void flipCard(final ViewSwitcher card) {
        // Apply flip animation
        Animation flip = AnimationUtils.loadAnimation(this, R.anim.flip);
        Animation midFlip = AnimationUtils.loadAnimation(this, R.anim.flip_middle);
        flip.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isFlipping=true;
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
                card.showNext();
                View currentView = card.getCurrentView();
                if (currentView instanceof ImageView) {
                    Object tag = currentView.getTag();
                    if (tag != null && tag instanceof Integer) {
                        flippedCardTemp = (int) tag;
                        if(cardIsHazard()){
                            card.setClickable(false);
                        }
                    }
                }
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if(health==0){
                    defeatScreen.setVisibility(View.VISIBLE);
                    mainGameScreen.setVisibility(View.GONE);
                }
                isFlipping = false;
                if(flippedCardB!=null && flippedCardA!=null){
                    cardComparator();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        card.startAnimation(flip);
    }
    private boolean cardIsHazard(){
        if(flippedCardTemp == R.drawable.mimic || flippedCardTemp == R.drawable.bomber || flippedCardTemp == R.drawable.poison){
            damage();
            if(flippedCardA != null && flippedCardB == null){
                flippedCardA = null;
            } else if (flippedCardA != null && flippedCardB != null){
                flippedCardB = null;
            }
            return true;
        }
        return false;
    }

    public void damage(){
            switch(health){
                case 3:
                    MediaPlayer.create(GameActivity.this, R.raw.aray).start();
                    health--;
                    Glide.with(this).load(R.drawable.hp_icon_dmg).into(hp_icon3);
                    break;
                case 2:
                    MediaPlayer.create(GameActivity.this, R.raw.ouch).start();
                    health--;
                    Glide.with(this).load(R.drawable.hp_icon_dmg).into(hp_icon2);
                    break;
                case 1:
                    MediaPlayer.create(GameActivity.this, R.raw.haha_bubu).start();
                    health--;
                    Glide.with(this).load(R.drawable.hp_icon_dmg).into(hp_icon1);
                    break;
            }
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
                        gameInProgress = true;
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
                    if (cdSec*2 >= 100) {
                        cdRunning = !cdRunning;
                        handler.removeCallbacksAndMessages(null);
                        mainGameScreen.setVisibility(View.GONE);
                        defeatScreen.setVisibility(View.VISIBLE);
                        pauseMusic();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    // Initialization Methods and Utils
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

        //Layouts
        loadingScreen = findViewById(R.id.loadingScreen);
        mainGameScreen = findViewById(R.id.mainGameScreen);
        pausedScreen = findViewById(R.id.pausedScreen);
        defeatScreen = findViewById(R.id.defeatScreen);

        //Buttons
        btnPause = findViewById(R.id.btnPause);
    }

    private ImageView getCard(int index) {
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
}
