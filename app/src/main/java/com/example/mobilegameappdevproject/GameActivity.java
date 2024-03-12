package com.example.mobilegameappdevproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

        gameBGM = MediaPlayer.create(GameActivity.this, R.raw.red_barons_theme);
        gameBGM.setLooping(true);
        gameBGM.start();

        //Initialize Views
        hp_icon1 = findViewById(R.id.hp1);
        hp_icon2 = findViewById(R.id.hp2);
        hp_icon3 = findViewById(R.id.hp3);
        progressBar = findViewById(R.id.progressBar);
        txtTimer = findViewById(R.id.txtTimer);

        //Initialize GIFS
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon1);
        Glide.with(this).load(R.drawable.hp_icon).into(hp_icon2);
        Glide.with(this).load(R.drawable.hp_icon_dmg).into(hp_icon3);

        //Start Timer
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
}