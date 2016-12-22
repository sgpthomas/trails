package com.ksatgames.trails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by aditi on 12/22/16.
 */

public class LevelClearedScreen extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_level_cleared);
        }

        public void playAgain(View view) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
}