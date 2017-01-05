package com.ksatgames.trails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.content.DialogInterface;

import come.ksatgame.trails.gameEngine.GameView;

public class MainActivity extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;
    int totScore;
    int level;
    boolean endless;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totScore = getIntent().getIntExtra("TOT_SCORE", 0);
        level = getIntent().getIntExtra("LEVEL", 1);
        endless=getIntent().getBooleanExtra("ENDLESS", false);
        // Initialize gameView and set it as the view
        //number of screenlengths long the maze is
        int numBlocks=1;
        if(level<5) {
            numBlocks+=level;
        }
        else    {
            numBlocks+=2+level/2;
        }
        int speedPerSec;
        if(level<5) {
            speedPerSec = 200 + 40*level;
        }
        else    {
            speedPerSec = 280 + 20*level;
        }
        gameView = new GameView(this, numBlocks, speedPerSec, level, totScore, endless);
        setContentView(gameView);
    }
    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();
        // Tell the gameView resume method to execute
        gameView.resume();
    }
    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        // Tell the gameView pause method to execute
        gameView.pause();
    }

    @Override
    public void onBackPressed() {
        gameView.pause();
//        gameView.drawPause();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("End game")
                .setMessage("Are you sure you want to end this game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
