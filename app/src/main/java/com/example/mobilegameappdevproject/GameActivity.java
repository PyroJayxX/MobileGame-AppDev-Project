package com.example.mobilegameappdevproject;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private MediaPlayer gameBGM;
    public ImageView hp_icon1;
    public ImageView hp_icon2;
    public ImageView hp_icon3;
    public ProgressBar progressBar;
    public TextView txtTimer;
    public FrameLayout loadingScreen;
    private ViewSwitcher card1, card2, card3, card4, card5,card6, card7,
            card8, card9, card10, card11, card12, card13, card14, card15;
    private boolean isFront = false;

    private int counter = 0;

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
        initialTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameBGM != null) {
            gameBGM.stop();
            gameBGM.release();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (gameBGM != null && gameBGM.isPlaying()) {
            gameBGM.pause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (gameBGM != null && !gameBGM.isPlaying()) {
            gameBGM.start();
        }
    }


    //GameScreen Timer Methods
    public void initialTimer() {
        Timer t = new Timer();
        counter = 7;
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        counter--;
                        txtTimer.setText(String.valueOf(counter-1));
                        if (counter == 1) {
                            txtTimer.setText(R.string.go_text);
                        }
                        if (counter <= 0) {
                            t.cancel();
                            txtTimer.setVisibility(View.INVISIBLE);
                            countdown();
                        }
                    }
                });
            }
        };
        t.schedule(tt, 0, 1000);
    }

    public void countdown() {
        Timer t = new Timer();
        counter = 0;
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                counter++;
                progressBar.setProgress(counter);
                if (counter == 100) {
                    t.cancel();
                }
            }
        };
        t.schedule(tt, 0, 350);
    }

    // GameScreen Buttons
    public void btnBack(View v) {
        if (gameBGM != null) {
            gameBGM.stop();
            gameBGM.release();
            gameBGM = null;
        }
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
    public void btnRetry(View v){
        if (gameBGM != null) {
            gameBGM.stop();
            gameBGM.release();
            gameBGM = null;
        }
        finish();
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    // Initalization Methods
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
        Glide.with(this).load(R.drawable.hp_icon_dmg).into(hp_icon3);

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
    }

    private void startMusic(){
        gameBGM = MediaPlayer.create(GameActivity.this, R.raw.red_barons_theme);
        gameBGM.setLooping(true);
        gameBGM.start();
    }

    // Card Logic
    public void onCardClick(View view) {
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
                card1.startAnimation(midFlip);
            }
            @Override
            public void onAnimationRepeat(Animation animation){
            }
        });

        midFlip.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (isFront) {
                    card1.showNext(); // Switch to the back card
                } else {
                    card1.showPrevious(); // Switch to the front card
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
        card1.startAnimation(flip);
    }

}