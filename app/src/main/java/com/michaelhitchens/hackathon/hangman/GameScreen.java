package com.michaelhitchens.hackathon.hangman;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public final class GameScreen extends AppCompatActivity {
    final private static String CUSTOM_WORD_KEY = "SECRET_WORD";

    private GameState game;

    private TextView wordDisplayLabel;
    private ImageView gallowsImage;

    public static Intent intentFromWord(Context ctx, String word) {
        Intent intent = new Intent(ctx, GameScreen.class);
        intent.putExtra(GameScreen.CUSTOM_WORD_KEY, word);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        PropertyEnforcer.enforceOrientation(this);

        registerShowKeyboardButton();
        registerVirtualKeyboard();

        PropertyEnforcer.enforceSoftKeyboard(this);

        wordDisplayLabel = findViewById(R.id.secretWordLabel);
        gallowsImage = findViewById(R.id.gallowsImage);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            showToast("ERROR: Malformed Intent!");
            finish();
            return;
        }

        String word = extras.getString(CUSTOM_WORD_KEY, null);
        if (word == null) {
            showToast("ERROR: Malformed Intent!");
            finish();
            return;
        }

        game = new GameState(word);
        updateDisplay();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        game.saveState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        game.loadState(state);

        updateImage();
        updateDisplay();
        rebuildRemainingText();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String keyString = KeyEvent.keyCodeToString(keyCode);

        if (!isKeyStringALetter(keyString)) {
            return super.onKeyUp(keyCode, event);
        }

        char letter = getLetterFromKeyString(keyString);
        if (game.isAlreadyGuessed(letter)) {
            showToast("Already guessed!");
            return super.onKeyUp(keyCode, event);
        }

        game.guess(letter);
        removeFromRemainingText(letter);

        updateDisplay();
        updateImage();

        if (game.hasWon()) {
            showWinScreen();
        } else if (game.hasLost()) {
            showLoseScreen();
        }

        return super.onKeyUp(keyCode, event);
    }

    private static boolean isKeyStringALetter(String keyString) {
        char letter = getLetterFromKeyString(keyString);
        char letterBefore = keyString.charAt(keyString.length()-2);
        return letter >= 'A' && letter <= 'Z' && letterBefore == '_';
    }

    private static char getLetterFromKeyString(String keyString) {
        return keyString.charAt(keyString.length()-1);
    }

    public void showWinScreen() {
        startActivity(new Intent(this, WinActivity.class));
        PropertyEnforcer.enforceSoftKeyboard(this);
        finish();
    }

    public void showLoseScreen() {
        startActivity(LoseActivity.intentFromAnswer(this, game.getSecretWordOnLoss()));
        PropertyEnforcer.enforceSoftKeyboard(this);
        finish();
    }

    public void updateImage() {
        int wrong = game.getNumOfWrongGuesses();

        switch ( wrong ) {
            case 0: gallowsImage.setImageResource(R.drawable.gallows0); break;
            case 1: gallowsImage.setImageResource(R.drawable.gallows1); break;
            case 2: gallowsImage.setImageResource(R.drawable.gallows2); break;
            case 3: gallowsImage.setImageResource(R.drawable.gallows3); break;
            case 4: gallowsImage.setImageResource(R.drawable.gallows4); break;
            case 5: gallowsImage.setImageResource(R.drawable.gallows5); break;
            case 6: gallowsImage.setImageResource(R.drawable.gallows6); break;
            case 7: gallowsImage.setImageResource(R.drawable.gallows7); break;
            case 8: gallowsImage.setImageResource(R.drawable.gallows8); break;
        }
    }

    private void registerShowKeyboardButton() {
        Button button = findViewById(R.id.showKeyboardButton);
        if (button == null) {
            System.out.println("WARNING: Can't register keyboard toggle!");
            return;
        }

        button.setOnClickListener((v) -> {
            InputMethodManager inputMethodManager =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager == null) {
                showToast("Ooops! Can't show keyboard!");
                return;
            }

            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        });
    }

    private void removeFromRemainingText(char toRemove) {
        TextView remaining = findViewById( R.id.remainingText );
        if ( remaining == null ) {
            return;
        }

        String remainingString = remaining.getText().toString();

        char decomposed[] = remainingString.toCharArray();
        for ( int i = 0; i < decomposed.length; i++ ) {
            if ( decomposed[i] == toRemove ) {
                decomposed[i] = ' ';
                remaining.setText( new String( decomposed ) );
                return;
            }
        }

        System.out.println( "WARNING: Reremoving " + toRemove );
    }

    public void showToast( String message ) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity( Gravity.TOP, 0, 0 );
        toast.show();
    }

    public void updateDisplay() {
        wordDisplayLabel.setText( game.getDisplay() );
    }

    public void rebuildRemainingText() {
        String wrongGuesses = game.getWrongGuesses();
        for ( int i = 0; i < wrongGuesses.length(); i++ ) {
            removeFromRemainingText( wrongGuesses.charAt( i ) );
        }

        String rightGuesses = game.getRightGuesses();
        for ( int i = 0; i < rightGuesses.length(); i++ ) {
            removeFromRemainingText( rightGuesses.charAt( i ) );
        }
    }

    private void registerVirtualKeyboard() {
        registerLetterButton(R.id.button_a);
        registerLetterButton(R.id.button_b);
        registerLetterButton(R.id.button_c);
        registerLetterButton(R.id.button_d);
        registerLetterButton(R.id.button_e);
        registerLetterButton(R.id.button_f);
        registerLetterButton(R.id.button_g);
        registerLetterButton(R.id.button_h);
        registerLetterButton(R.id.button_i);
        registerLetterButton(R.id.button_j);
        registerLetterButton(R.id.button_k);
        registerLetterButton(R.id.button_l);
        registerLetterButton(R.id.button_m);
        registerLetterButton(R.id.button_n);
        registerLetterButton(R.id.button_o);
        registerLetterButton(R.id.button_p);
        registerLetterButton(R.id.button_q);
        registerLetterButton(R.id.button_r);
        registerLetterButton(R.id.button_s);
        registerLetterButton(R.id.button_t);
        registerLetterButton(R.id.button_u);
        registerLetterButton(R.id.button_v);
        registerLetterButton(R.id.button_w);
        registerLetterButton(R.id.button_x);
        registerLetterButton(R.id.button_y);
        registerLetterButton(R.id.button_z);
    }

    private void registerLetterButton(int id) {
        Button button = findViewById(id);
        if ( button == null ) {
            return;
        }

        button.setOnClickListener(v -> {
            Button b = (Button)v;
            char letter = b.getText().charAt(0);

            if (game.isAlreadyGuessed(letter)) {
                showToast( "Already guessed!" );
                return;
            }

            game.guess(letter);
            b.setVisibility(View.INVISIBLE);

            updateDisplay();
            updateImage();

            if (game.hasWon()) {
                showWinScreen();
            } else if (game.hasLost()) {
                showLoseScreen();
            }
        });
    }

}
