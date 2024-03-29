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
    public static void cardComparator(GameActivity gameActivity, ViewSwitcher cardA, ViewSwitcher cardB) {
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
            if (gameActivity.flippedCardCount == 6) {
                gameActivity.isVictory = true;
                gameActivity.gameInProgress = false;
                gameActivity.cdOnPause();
                gameActivity.txtScore.setText(Integer.toString(gameActivity.Score));
                gameActivity.mainGameScreen.setVisibility(View.GONE);
                gameActivity.victoryScreen.setVisibility(View.VISIBLE);
                gameActivity.soundManager.playSoundEffect(R.raw.sfx_victory);
            }
        } else if (cardAImageTag != null && cardBImageTag != null && !cardAImageTag.equals(cardBImageTag)) {
            flipCard(gameActivity, cardA);
            flipCard(gameActivity, cardB);
            gameActivity.soundManager.playSoundEffect(R.raw.sfx_notmatch);
            gameActivity.flippedCardA = null;
            gameActivity.flippedCardB = null;
        }
    }

    public static boolean cardIsHazard(GameActivity gameActivity) {
        if (gameActivity.flippedCardTemp == R.drawable.mimic || gameActivity.flippedCardTemp == R.drawable.bomber || gameActivity.flippedCardTemp == R.drawable.poison) {
            gameActivity.soundManager.playSoundEffect(R.raw.sfx_ouch);
            if (gameActivity.isMortal) {
                gameActivity.damage();
            }
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
        // Apply flip animation
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
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gameActivity.isFlipping = false;
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

}
