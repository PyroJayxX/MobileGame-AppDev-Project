package com.example.mobilegameappdevproject;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    private static SoundManager instance;
    static MediaPlayer backgroundMusicPlayer;
    private Context appContext;
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
    public void playBackgroundMusic(int musicResourceID) {
        // Method to start playing background music
        stopBackgroundMusic();
        backgroundMusicPlayer = MediaPlayer.create(appContext, musicResourceID);
        backgroundMusicPlayer.setLooping(true);
        backgroundMusicPlayer.start();
    }
    public void stopBackgroundMusic() {
        // Method to stop background music
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
    }
    public void pauseBackgroundMusic() {
        // Method to pause background music
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.pause();
        }
    }
    public void resumeBackgroundMusic() {
        // Method to resume background music
        if (backgroundMusicPlayer != null && !backgroundMusicPlayer.isPlaying()) {
            backgroundMusicPlayer.start();
        }
    }
    public void playSoundEffect(int soundResourceID) {
        // Method to play a sound effect
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
