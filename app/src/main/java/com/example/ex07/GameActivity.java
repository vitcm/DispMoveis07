package com.example.ex07;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private ImageView pigImage, coinImage;
    private TextView scoreText;
    private float pigX, pigY;
    private Handler handler = new Handler();
    private Random random = new Random();
    private int score = 0;
    private boolean gameFinished = false;
    private boolean coinCollisionOccurred = false;
    private Runnable coinDisplayRunnable;
    private MediaPlayer coinSound, upSound, downSound, leftSound, rightSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        pigImage = findViewById(R.id.pigImage);
        coinImage = findViewById(R.id.coinImage);
        scoreText = findViewById(R.id.scoreText);

        coinSound = MediaPlayer.create(this, R.raw.coin);
        upSound = MediaPlayer.create(this, R.raw.north);
        downSound = MediaPlayer.create(this, R.raw.south);
        leftSound = MediaPlayer.create(this, R.raw.west);
        rightSound = MediaPlayer.create(this, R.raw.east);

        pigImage.post(new Runnable() {
            @Override
            public void run() {
                pigX = pigImage.getX();
                pigY = pigImage.getY();
            }
        });

        findViewById(R.id.gameLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!gameFinished) {
                    int action = event.getAction();
                    float touchX = event.getX();
                    float touchY = event.getY();

                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            movePig(touchX, touchY);
                            return true;
                    }
                }
                return false;
            }
        });

        findViewById(R.id.gameLayout).post(new Runnable() {
            @Override
            public void run() {
                Log.d("Display", "Chamei no run" );
                displayCoin();
                startGameTimer();
            }
        });
    }

    private void movePig(float touchX, float touchY) {
        if (touchX < pigX) {
            pigX -= 30;
            playLeftSound();
        } else if (touchX > pigX + pigImage.getWidth()) {
            pigX += 30;
            playRightSound();
        }
        if (touchY < pigY) {
            pigY -= 30;
            playUpSound();
        } else if (touchY > pigY + pigImage.getHeight()) {
            pigY += 30;
            playDownSound();
        }

        pigImage.setX(pigX);
        pigImage.setY(pigY);

        checkCoinCollision();
    }

    private void displayCoin() {
        Log.d("Display", "Entrei no display" );
        int parentWidth = findViewById(R.id.gameLayout).getWidth();
        int parentHeight = findViewById(R.id.gameLayout).getHeight();

        if (parentWidth > 0 && parentHeight > 0) {
            int coinX = random.nextInt(parentWidth - coinImage.getWidth());
            int coinY = random.nextInt(parentHeight - coinImage.getHeight());

            coinImage.setX(coinX);
            coinImage.setY(coinY);
            coinImage.setVisibility(View.VISIBLE);

            // Cancelar o Runnable anterior, se houver
            if (coinDisplayRunnable != null) {
                handler.removeCallbacks(coinDisplayRunnable);
            }

            // Programar um novo Runnable
            coinDisplayRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!coinCollisionOccurred) {
                        coinImage.setVisibility(View.GONE);
                        Log.d("Display", "Chamei no display" );
                        displayCoin();
                    }
                }
            };

            handler.postDelayed(coinDisplayRunnable, 6000);
        }
    }

    private void checkCoinCollision() {
        coinCollisionOccurred=false;
        float coinX = coinImage.getX();
        float coinY = coinImage.getY();
        float pigWidth = pigImage.getWidth();
        float pigHeight = pigImage.getHeight();

        if (pigX < coinX + coinImage.getWidth() && pigX + pigWidth > coinX &&
                pigY < coinY + coinImage.getHeight() && pigY + pigHeight > coinY) {
            coinImage.setVisibility(View.GONE);
            if (!gameFinished) {
                score += 10;
                updateScore();
                playCoinSound();
            }
            Log.d("Display", "Chamei no checkcoin" );
            coinCollisionOccurred = true;
            displayCoin();
        }
    }

    private void updateScore() {
        scoreText.setText( String.valueOf(score));
    }

    private long startTime;

    private void startGameTimer() {
        startTime = System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameFinished = true;
                saveScore(score);
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("SCORE", score);
                startActivity(intent);
                finish();
            }
        }, 30000);
    }

    private void saveScore(int score) {
        SharedPreferences preferences = getSharedPreferences("game_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int highestScore = preferences.getInt("highest_score", 0);
        String latestScores = preferences.getString("latest_scores", "");

        if (score > highestScore) {
            editor.putInt("highest_score", score);
        }

        String updatedScores = updateLatestScores(latestScores, score);
        editor.putString("latest_scores", updatedScores);

        editor.apply();
    }

    private String updateLatestScores(String latestScores, int newScore) {
        String[] scores = latestScores.split(",");
        LinkedList<String> scoreList = new LinkedList<>(Arrays.asList(scores));

        scoreList.addFirst(String.valueOf(newScore));

        if (scoreList.size() > 3) {
            scoreList.removeLast();
        }
        return TextUtils.join(",", scoreList);
    }

    private void playCoinSound() {
        if (coinSound != null) {
            coinSound.start();
        }
    }

    private void playUpSound() {
        if (upSound != null) {
            upSound.start();
        }
    }

    private void playDownSound() {
        if (downSound != null) {
            downSound.start();
        }
    }

    private void playLeftSound() {
        if (leftSound != null) {
            leftSound.start();
        }
    }

    private void playRightSound() {
        if (rightSound != null) {
            rightSound.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (coinSound != null) {
            coinSound.release();
            coinSound = null;
        }
        if (upSound != null) {
            upSound.release();
            upSound = null;
        }
        if (downSound != null) {
            downSound.release();
            downSound = null;
        }
        if (leftSound != null) {
            leftSound.release();
            leftSound = null;
        }
        if (rightSound != null) {
            rightSound.release();
            rightSound = null;
        }
    }
}
