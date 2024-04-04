package com.example.mobilegameappdevproject;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardUtils {

    public static void shuffleCards(GameActivity gameActivity) {
        // Shuffle cards function depending on game mode
        if(gameActivity.gameMode.equals("veteran")){
        Integer[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5,
                R.drawable.image6, R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
                R.drawable.image5, R.drawable.image6, R.drawable.mimic, R.drawable.bomber, R.drawable.poison};

        List<Integer> imageIndex = new ArrayList<>(Arrays.asList(images));
        Collections.shuffle(imageIndex);

        for (int i = 1; i <= 15; i++) {
            ImageView card = gameActivity.getCard(i);
            if (card != null) {
                int resourceId = imageIndex.get(i - 1);
                if (resourceId == R.drawable.mimic) {
                    Glide.with(gameActivity).load(R.drawable.mimic).into(card);
                } else if (resourceId == R.drawable.bomber) {
                    Glide.with(gameActivity).load(R.drawable.bomber).into(card);
                } else if (resourceId == R.drawable.poison) {
                    Glide.with(gameActivity).load(R.drawable.poison).into(card);
                } else {
                    card.setImageResource(resourceId);
                }
                card.setTag(resourceId);
            }
        }
        }

        if(gameActivity.gameMode.equals("novice")){
            Integer[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5,
                    R.drawable.image6, R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
                    R.drawable.image5, R.drawable.image6};

            List<Integer> imageIndex = new ArrayList<>(Arrays.asList(images));
            Collections.shuffle(imageIndex);

            for (int i = 1; i <= 12; i++) {
                ImageView card = gameActivity.getCard(i);
                if (card != null) {
                    int resourceId = imageIndex.get(i - 1);
                    card.setImageResource(resourceId);
                    card.setTag(resourceId);
                }
            }
        }

        if(gameActivity.gameMode.equals("master")){
            Integer[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6,
                    R.drawable.image7, R.drawable.image8, R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5,
                    R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.mimic, R.drawable.bomber, R.drawable.poison, R.drawable.trap};

            List<Integer> imageIndex = new ArrayList<>(Arrays.asList(images));
            Collections.shuffle(imageIndex);

            for (int i = 1; i <= 20; i++) {
                ImageView card = gameActivity.getCard(i);
                if (card != null) {
                    int resourceId = imageIndex.get(i - 1);
                    if (resourceId == R.drawable.mimic) {
                        Glide.with(gameActivity).load(R.drawable.mimic).into(card);
                    } else if (resourceId == R.drawable.bomber) {
                        Glide.with(gameActivity).load(R.drawable.bomber).into(card);
                    } else if (resourceId == R.drawable.poison) {
                        Glide.with(gameActivity).load(R.drawable.poison).into(card);
                    } else if (resourceId == R.drawable.trap){
                        Glide.with(gameActivity).load(R.drawable.trap).into(card);
                    } else {
                        card.setImageResource(resourceId);
                    }
                    card.setTag(resourceId);
                }
            }
        }
    }
    public static void cardComparator(GameActivity gameActivity, ViewSwitcher cardA, ViewSwitcher cardB) {
        // Compares if the tag between two cards opened are equal
        View cardAImage = cardA.getCurrentView();
        View cardBImage = cardB.getCurrentView();
        Object cardAImageTag = null;
        Object cardBImageTag = null;
        if (cardAImage instanceof ImageView) {
            cardAImageTag = cardAImage.getTag();
        }
        if (cardBImage instanceof ImageView) {
            cardBImageTag = cardBImage.getTag();
        }
        if (cardAImageTag != null && cardBImageTag != null && cardAImageTag.equals(cardBImageTag)) {
            gameActivity.soundManager.playSoundEffect(R.raw.sfx_cardmatch);
            cardA.setClickable(false);
            cardB.setClickable(false);
            gameActivity.flippedCardA = null;
            gameActivity.flippedCardB = null;
            gameActivity.flippedCardCount++;
            victoryChecker(gameActivity);
        } else if (cardAImageTag != null && cardBImageTag != null && !cardAImageTag.equals(cardBImageTag)) {
            flipCard(gameActivity, cardA);
            flipCard(gameActivity, cardB);
            gameActivity.soundManager.playSoundEffect(R.raw.sfx_notmatch);
            gameActivity.flippedCardA = null;
            gameActivity.flippedCardB = null;
        }
    }

    public static boolean cardIsHazard(GameActivity gameActivity) {
        // Checks if card is an instance of a bomber, mimic, poison, or trap
        if (gameActivity.flippedCardTemp == R.drawable.mimic || gameActivity.flippedCardTemp == R.drawable.bomber || gameActivity.flippedCardTemp == R.drawable.poison || gameActivity.flippedCardTemp == R.drawable.trap) {
            gameActivity.soundManager.playSoundEffect(R.raw.sfx_ouch);
            gameActivity.damage();
            if (gameActivity.flippedCardA != null && gameActivity.flippedCardB == null) {
                gameActivity.flippedCardA = null;
            } else if (gameActivity.flippedCardA != null && gameActivity.flippedCardB != null) {
                gameActivity.flippedCardB = null;
            }
            return true;
        }
        return false;
    }

    public static void flipCard(GameActivity gameActivity, final ViewSwitcher card) {
        // Toggles card view between frontcard and backcard with animation
        Animation flip = AnimationUtils.loadAnimation(gameActivity, R.anim.flip);
        Animation midFlip = AnimationUtils.loadAnimation(gameActivity, R.anim.flip_middle);
        flip.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                gameActivity.soundManager.playSoundEffect(R.raw.sfx_cardflip);
                gameActivity.isFlipping = true;
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the midFlip animation after the first animation ends
                card.startAnimation(midFlip);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        midFlip.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                card.showNext();
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                gameActivity.isFlipping = false;

                View currentView = card.getCurrentView();
                if (currentView instanceof ImageView) {
                    Object tag = currentView.getTag();
                    if (tag != null && tag instanceof Integer) {
                        gameActivity.flippedCardTemp = (int) tag;
                        if (cardIsHazard(gameActivity)) {
                            card.setClickable(false);
                            gameActivity.hazardCardCount++;
                        }
                    }
                }
                gameActivity.Score = gameActivity.calculateScore(100 - gameActivity.cdSec, gameActivity.hazardCardCount);
                if (gameActivity.flippedCardB != null && gameActivity.flippedCardA != null) {
                    cardComparator(gameActivity, gameActivity.flippedCardA, gameActivity.flippedCardB);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        card.startAnimation(flip);
    }

    public static void victoryChecker(GameActivity gameActivity) {
        // Checks if user has won
        int requiredMatches = 6; // Default for novice and veteran modes
        if (gameActivity.gameMode.equals("master")) {
            requiredMatches = 8; // For master mode
        }
        if (gameActivity.flippedCardCount == requiredMatches) {
            gameActivity.soundManager.playSoundEffect(R.raw.sfx_victory);
            gameActivity.isVictory = true;
            gameActivity.gameInProgress = false;
            gameActivity.txtScore.setText(Integer.toString(gameActivity.Score));
            if (gameActivity.Score > gameActivity.currentHighScore) {
                gameActivity.currentHighScore = gameActivity.Score;
                // Update the local high score for the current game mode
                gameActivity.updateLocalHighScore(gameActivity.gameMode, gameActivity.currentHighScore);
            }
            gameActivity.txtHighScore.setText(Integer.toString(gameActivity.currentHighScore));
            gameActivity.mainGameScreen.setVisibility(View.GONE);
            gameActivity.victoryScreen.setVisibility(View.VISIBLE);
        }
    }
}
