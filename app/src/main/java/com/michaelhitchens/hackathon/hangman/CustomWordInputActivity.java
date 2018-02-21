package com.michaelhitchens.hackathon.hangman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * An activity to handle creating a game session from a user-supplied word.
 */
public final class CustomWordInputActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_word);
        PropertyEnforcer.enforceOrientation(this);

        Button button = findViewById(R.id.confirmButton);
        button.setOnClickListener(v -> {
            EditText input = findViewById(R.id.customWordText);
            String word = formatInput(input.getText().toString());
            if (word.length() > 0) {
                startGameWithWord(word);
            }
        });
    }

    /**
     * Format user input to something expected.
     *
     * Involves turning everything to uppercase and trimming whitespace (while preserving spaces in
     * middle). If something is not a character in the English alphabet then it is discarded.
     *
     * @param text The user-supplied text
     * @return User input safe to be used with the game.
     */
    private String formatInput(String text) {
        String formatted = "";

        String trimmed = text.trim();
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Utilities.isLetter(c)) {
                formatted += Utilities.toUpperCase(c);
            } else if (c == ' ') {
                formatted += ' ';
            }
        }

        return formatted;
    }

    /**
     * Launch the game with the given formatted word
     * @param word A word passed through formatInput()
     */
    private void startGameWithWord(String word) {
        startActivity(GameScreen.intentFromWord(this, word));
        finish();
    }

    /**
     * Namespace to hold utility functions
     */
    private static class Utilities {
        private static boolean isLetter(char c) {
            return isLowerCaseLetter(c) || isUpperCaseLetter(c);
        }

        private static boolean isUpperCaseLetter(char c) {
            return c >= 'A' && c <= 'Z';
        }

        private static boolean isLowerCaseLetter(char c) {
            return c >= 'a' && c <= 'z';
        }

        private static char toUpperCase(char c) {
            if (isUpperCaseLetter(c)) return c;
            return (char)(c - 'a' + 'A');
        }
    }
}
