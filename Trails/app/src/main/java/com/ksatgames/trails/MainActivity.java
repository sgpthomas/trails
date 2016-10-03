package com.ksatgames.trails;

import android.os.Bundle;

import android.app.Activity;

import come.ksatgame.trails.gameEngine.GameView;

public class MainActivity extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);

    }
//max is maximum number of obstacles per line. assumption: less then half of width
    int[][] genMatrix(int l, int w,int max) //generated original matrix; to be called before start of game
    {
        int matrix[][]=new int[l][w];
       for(int i=0; i<l; i++) {
           int[] a=new int[max];
           for (int j = 0; j < max; j++) {
               a[j] = (int) Math.random() * l;
           }
           int k=0;
           for(int j=0; j<w; j++)
               if(j==a[k])
               {
                   matrix[i][j]=a[k++];
               }
       }
    return matrix;
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

}
