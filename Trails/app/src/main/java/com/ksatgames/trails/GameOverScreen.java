package com.ksatgames.trails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverScreen extends AppCompatActivity {

    int levelSelection;
    boolean endless;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_screen);
        int score = getIntent().getIntExtra("TOT_SCORE", 0);
        levelSelection=getIntent().getIntExtra("LEVEL_SELECTED", 0);
        endless=getIntent().getBooleanExtra("ENDLESS", false);
        TextView scoreText = (TextView)findViewById(R.id.score_text);
        scoreText.setText("Total Score : "+score);
    }

    public void playAgain(View view) {
        Intent intent;
        if (levelSelection > 0) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("LEVEL", levelSelection);
            intent.putExtra("LEVEL_SELECTED", levelSelection);
            intent.putExtra("ENDLESS", endless);
        }
        else  {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("ENDLESS", endless);
        }
        startActivity(intent);
    }

    public void levelSelection(View view) {
        Intent intent = new Intent(this, LevelSelectionScreen.class);
        intent.putExtra("ENDLESS", endless);
        startActivity(intent);
    }

    public void home(View view) {
        Intent intent=new Intent(this, Start.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
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
