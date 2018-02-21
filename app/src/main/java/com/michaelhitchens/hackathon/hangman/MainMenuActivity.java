package com.michaelhitchens.hackathon.hangman;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public final class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        registerDifficultyButton(R.id.easyButton, Difficulty.EASY);
        registerDifficultyButton(R.id.normalButton, Difficulty.NORMAL);
        registerDifficultyButton(R.id.hardButton, Difficulty.HARD);

        Button button = findViewById( R.id.customButton );
        button.setOnClickListener(v -> startActivity(new Intent(this, CustomWordInputActivity.class)));
    }

    public void registerDifficultyButton(int id, Difficulty difficulty) {
        Button button = findViewById(id);
        button.setOnClickListener(v -> startActivity(GameScreen.intentFromWord(this,
                WordRetriever.getWord(getResources(), difficulty))));
    }
}
