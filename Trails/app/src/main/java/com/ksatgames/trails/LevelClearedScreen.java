package com.ksatgames.trails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by aditi on 12/22/16.
 */

public class LevelClearedScreen extends AppCompatActivity {

    int score;
    int totScore;
    int level;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            score = getIntent().getIntExtra("SCORE", 0);
            totScore = getIntent().getIntExtra("TOT_SCORE", 0);
            level = getIntent().getIntExtra("LEVEL", 0);
            setContentView(R.layout.activity_level_cleared);
            TextView scoreText = (TextView)findViewById(R.id.score_text);
            scoreText.setText("Score : "+score);

            TextView levelCleared= (TextView)findViewById(R.id.level_cleared_text);
            levelCleared.setText("Level "+level+" Cleared!");

            totScore=totScore+score;
            TextView totalScore= (TextView)findViewById(R.id.tot_score);
            totalScore.setText("Total Score : "+totScore);

            level++;
        }

        public void levelCleared(View view) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("LEVEL", level);
            intent.putExtra("TOT_SCORE", totScore);
            startActivity(intent);
        }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to exit Trails?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}