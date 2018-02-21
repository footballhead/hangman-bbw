package com.michaelhitchens.hackathon.hangman;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public final class LoseActivity extends AppCompatActivity {
    final private static String ANSWER_KEY = "ANSWER";

    public static Intent intentFromAnswer(Context ctx, String answer) {
        Intent intent = new Intent(ctx, LoseActivity.class);
        intent.putExtra(ANSWER_KEY, answer);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);
        PropertyEnforcer.enforceOrientation(this);

        Button b = findViewById(R.id.mainMenuButton);
        b.setOnClickListener(v -> finish());

        displayAnswer();
    }

    private void displayAnswer() {
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            return;
        }

        String answer = extra.getString(ANSWER_KEY, null);
        if (answer == null) {
            return;
        }

        TextView answerText = findViewById(R.id.answerText);
        answerText.setText(answer);
    }
}
