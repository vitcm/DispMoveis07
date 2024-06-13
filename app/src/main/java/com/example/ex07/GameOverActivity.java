package com.example.ex07;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        int score = getIntent().getIntExtra("SCORE", 0);

        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("Pontos: " + score);

        TextView highestScoreTextView = findViewById(R.id.highestScoreTextView);
        TextView latestScoreTextView = findViewById(R.id.latestScoreTextView);

        SharedPreferences preferences = getSharedPreferences("game_prefs", MODE_PRIVATE);
        int highestScore = preferences.getInt("highest_score", 0);
        String latestScores = preferences.getString("latest_scores", "");

        highestScoreTextView.setText("Maior Pontuação: \n" + highestScore);
        latestScoreTextView.setText("Últimas Pontuações: \n" + formatLatestScores(latestScores));

        TextView mainMenuTextView = findViewById(R.id.mainMenuTextView);
        mainMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String formatLatestScores(String latestScores) {
        if (latestScores.isEmpty()) {
            return "N/A";
        }

        String[] scores = latestScores.split(",");
        StringBuilder formattedScores = new StringBuilder();
        for (String score : scores) {
            formattedScores.append(score).append("\n");
        }

        return formattedScores.toString().trim();
    }

}