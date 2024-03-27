package com.example.mobilegameappdevproject;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer backgroundMusicPlayer;
    private Context appContext;

    // Private constructor to prevent instantiation outside the class
    private SoundManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    // Singleton pattern to ensure only one instance of SoundManager exists
    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    // Method to start playing background music
    public void playBackgroundMusic(int musicResourceID) {
        stopBackgroundMusic();
        backgroundMusicPlayer = MediaPlayer.create(appContext, musicResourceID);
        backgroundMusicPlayer.setLooping(true);
        backgroundMusicPlayer.start();
    }

    // Method to stop background music
    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
    }

    // Method to pause background music
    public void pauseBackgroundMusic() {
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.pause();
        }
    }

    // Method to resume background music
    public void resumeBackgroundMusic() {
        if (backgroundMusicPlayer != null && !backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.start();
        }
    }

    // Method to play a sound effect
    public void playSoundEffect(int soundResourceID) {
        MediaPlayer soundEffectPlayer = MediaPlayer.create(appContext, soundResourceID);
        soundEffectPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release(); // Release the MediaPlayer after completion
            }
        });
        soundEffectPlayer.start();
    }
}
