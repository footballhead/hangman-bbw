package com.hackathon.michael.hangman;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class GameScreen extends ActionBarActivity {
    final static String DIFFICULTY_KEY = "GAME_DIFFICULTY";
    final static String CUSTOM_WORD_KEY = "SECRET_WORD";

    private GameState game;

    private TextView wordDisplayLabel;
    private ImageView gallowsImage;

    final public static String EASY_DIFFICULTY = "EASY";
    final public static String NORMAL_DIFFICULTY = "NORMAL";
    final public static String HARD_DIFFICULTY = "HARD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game_screen);

        WordRetriever.setResources( getResources() );

        registerShowKeyboardButton();

        wordDisplayLabel = ( TextView )findViewById( R.id.secretWordLabel );
        gallowsImage = ( ImageView )findViewById( R.id.gallowsImage );

        try {
            actOnIntent();
        } catch ( Exception e ) {
            initFromWord( "ERROR" );
            showToast("ERROR: read exception!");
        }
    }

    @Override
    protected void onSaveInstanceState( Bundle bundle ) {
        game.saveState( bundle );
    }

    @Override
    protected void onRestoreInstanceState( Bundle state ) {
        game.loadState( state );

        updateImage();
        updateDisplay();
        rebuildRemainingText();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String keyString = KeyEvent.keyCodeToString( keyCode );

        if ( isKeyStringALetter( keyString ) ) {
            char letter = getLetterFromKeyString( keyString );

            if ( game.isAlreadyGuessed( letter ) ) {
                showToast( "Already guessed!" );
            } else {
                if ( game.guess( letter ) ) {
                    showToast("RIGHT!");
                    updateDisplay();
                    removeFromRemainingText( letter );
                    checkWinCondition();
                } else {
                    showToast("WRONG!");
                    updateImage();
                    removeFromRemainingText( letter );
                    checkLoseCondition();
                }
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    private boolean isKeyStringALetter( String keyString ) {
        char letter = getLetterFromKeyString( keyString );
        char letterBefore = keyString.charAt( keyString.length()-2 );
        return letter >= 'A' && letter <= 'Z' && letterBefore == '_';
    }

    private char getLetterFromKeyString( String keyString ) {
        return keyString.charAt( keyString.length()-1 );
    }


    private void actOnIntent() throws IOException {
        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            String difficulty = extras.getString( DIFFICULTY_KEY, null );

            if ( difficulty != null ) {
                initFromDifficulty( difficulty );
            } else {
                String word = extras.getString( CUSTOM_WORD_KEY, null );

                if ( word != null ) {
                    initFromWord( word );
                } else {
                    initFromDifficulty( NORMAL_DIFFICULTY );
                    showToast("ERROR: Intent malformed!");
                }
            }
        }
    }

    private void initFromDifficulty( String difficulty ) throws IOException {
        if ( difficulty.equals( EASY_DIFFICULTY ) ) {
            initFromWord( WordRetriever.getEasyWord() );
        } else if ( difficulty.equals( NORMAL_DIFFICULTY ) ) {
            initFromWord( WordRetriever.getNormalWord() );
        } else if ( difficulty.equals( HARD_DIFFICULTY ) ) {
            initFromWord( WordRetriever.getHardWord() );
        } else {
            initFromWord( "ERROR" );
            showToast("ERROR: invalid difficulty");
        }
    }

    private void initFromWord( String word ) {
        game = new GameState( word );
        updateDisplay();
    }

    public void checkWinCondition() {
        if ( game.hasWon() ) {
            showWinScreen();
        }
    }

    public void checkLoseCondition() {
        if ( game.hasLost() ) {
            showLoseScreen();
        }
    }

    public void showWinScreen() {
        Intent intent = new Intent( this, WinActivity.class );
        startActivity( intent );
        finish();
    }

    public void showLoseScreen() {
        Intent intent = new Intent( this, LoseActivity.class );
        intent.putExtra( LoseActivity.ANSWER_KEY, game.getSecretWordOnLoss() );
        startActivity( intent );
        finish();
    }

    public void updateImage() {
        int wrong = game.getNumOfWrongGuesses();

        switch ( wrong ) {
            case 0: gallowsImage.setImageResource( R.drawable.gallows0 ); break;
            case 1: gallowsImage.setImageResource( R.drawable.gallows1 ); break;
            case 2: gallowsImage.setImageResource( R.drawable.gallows2 ); break;
            case 3: gallowsImage.setImageResource( R.drawable.gallows3 ); break;
            case 4: gallowsImage.setImageResource( R.drawable.gallows4 ); break;
            case 5: gallowsImage.setImageResource( R.drawable.gallows5 ); break;
            case 6: gallowsImage.setImageResource( R.drawable.gallows6 ); break;
        }
    }

    private void registerShowKeyboardButton() {
        Button button = (Button) findViewById( R.id.showKeyboardButton );

        if ( button == null ) {
            System.out.println( "WARNING: Can't register keyboard toggle!" );
            return;
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println( "SHOW KEYBOARD" );
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    private void removeFromRemainingText( char toRemove ) {
        TextView remaining = ( TextView )findViewById( R.id.remainingText );
        if ( remaining == null ) return;

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

}
