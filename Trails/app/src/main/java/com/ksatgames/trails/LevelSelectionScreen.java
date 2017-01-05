package com.ksatgames.trails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by aditi on 1/4/17.
 */

public class LevelSelectionScreen extends AppCompatActivity {

    EditText level;
    int l;
    boolean endless;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            endless=getIntent().getBooleanExtra("ENDLESS", false);
            setContentView(R.layout.activity_level_selection);
            level=(EditText)findViewById(R.id.level);
        }

        public void play(View view) {
            String text=level.getText().toString();
            try {
                l=Integer.parseInt(text);
            }
            catch (Exception e) {
                l=1;
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("LEVEL", l);
            if(endless) {
                intent.putExtra("ENDLESS", true);
            }
            startActivity(intent);
        }

}

