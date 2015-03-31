package com.michaelhitchens.hackathon.hangman;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

        if ( getResources().getBoolean(R.bool.portrait_only) ) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if ( getResources().getBoolean(R.bool.landscape_only) ){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        WordRetriever.setResources( getResources() );

        registerShowKeyboardButton();
        registerVirtualKeyboard();

        if ( getResources().getBoolean( R.bool.force_keyboard ) ) {
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

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
                    updateDisplay();
                    removeFromRemainingText( letter );
                    checkWinCondition();
                } else {
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
            case 7: gallowsImage.setImageResource( R.drawable.gallows7 ); break;
            case 8: gallowsImage.setImageResource( R.drawable.gallows8 ); break;
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

    private void registerVirtualKeyboard() {
        registerLetterButton( R.id.button_a );
        registerLetterButton( R.id.button_b );
        registerLetterButton( R.id.button_c );
        registerLetterButton( R.id.button_d );
        registerLetterButton( R.id.button_e );
        registerLetterButton( R.id.button_f );
        registerLetterButton( R.id.button_g );
        registerLetterButton( R.id.button_h );
        registerLetterButton( R.id.button_i );
        registerLetterButton( R.id.button_j );
        registerLetterButton( R.id.button_k );
        registerLetterButton( R.id.button_l );
        registerLetterButton( R.id.button_m );
        registerLetterButton( R.id.button_n );
        registerLetterButton( R.id.button_o );
        registerLetterButton( R.id.button_p );
        registerLetterButton( R.id.button_q );
        registerLetterButton( R.id.button_r );
        registerLetterButton( R.id.button_s );
        registerLetterButton( R.id.button_t );
        registerLetterButton( R.id.button_u );
        registerLetterButton( R.id.button_v );
        registerLetterButton( R.id.button_w );
        registerLetterButton( R.id.button_x );
        registerLetterButton( R.id.button_y );
        registerLetterButton( R.id.button_z );
    }

    private void registerLetterButton( int id ) {
        Button button = (Button) findViewById( id );

        if ( button == null ) return;

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button)v;
                char letter = b.getText().charAt( 0 );

                if ( game.isAlreadyGuessed( letter ) ) {
                    showToast( "Already guessed!" );
                } else {
                    if ( game.guess( letter ) ) {
                        updateDisplay();
                        b.setVisibility(View.INVISIBLE);
                        checkWinCondition();
                    } else {
                        updateImage();
                        b.setVisibility(View.INVISIBLE);
                        checkLoseCondition();
                    }
                }
            }
        });
    }

}
