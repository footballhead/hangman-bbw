package com.michaelhitchens.hackathon.hangman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public final class WinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        PropertyEnforcer.enforceOrientation(this);

        Button b = findViewById( R.id.mainMenuButton );
        b.setOnClickListener(v -> finish());
    }
}
