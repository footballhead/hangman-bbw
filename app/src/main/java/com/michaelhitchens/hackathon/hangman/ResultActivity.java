package com.michaelhitchens.hackathon.hangman;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public final class ResultActivity extends AppCompatActivity {
    final private static String ANSWER_KEY = "ANSWER";
    final private static String BIG_TEXT_KEY = "BIG_TEXT";

    public static Intent intentFromAnswer(Context ctx, String bigText, String answer) {
        Intent intent = new Intent(ctx, ResultActivity.class);
        intent.putExtra(ANSWER_KEY, answer);
        intent.putExtra(BIG_TEXT_KEY, bigText);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
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

        String bigText = extra.getString(BIG_TEXT_KEY, null);
        if (bigText == null) {
            return;
        }

        TextView answerText = findViewById(R.id.answerText);
        answerText.setText(answer);

        TextView bigTextView = findViewById(R.id.bigTextView);
        bigTextView.setText(bigText);
    }
}
