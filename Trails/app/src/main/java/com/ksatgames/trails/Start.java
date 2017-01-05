package com.ksatgames.trails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Start extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_begin);
        }

        public void play(View view) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        public void selectLevel(View view)  {
            Intent intent = new Intent(this, LevelSelectionScreen.class);
            startActivity(intent);
        }

        public void instructions(View view) {
            Intent intent = new Intent(this, InstructionsScreen.class);
            startActivity(intent);
        }

        public void endless(View view)  {
            Intent intent = new Intent(this, LevelSelectionScreen.class);
            intent.putExtra("ENDLESS", true);
            startActivity(intent);
        }
}
