package com.ksatgames.trails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by aditi on 1/4/17.
 */

public class InstructionsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
    }

    public void start(View view) {
        Intent intent = new Intent(this, Start.class);
        startActivity(intent);
//        finish();
    }
}