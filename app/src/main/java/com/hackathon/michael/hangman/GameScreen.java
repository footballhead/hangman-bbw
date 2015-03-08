package com.hackathon.michael.hangman;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class GameScreen extends ActionBarActivity {
    final static String DIFFICULTY_KEY = "GAME_DIFFICULTY";
    final static String CUSTOM_WORD_KEY = "SECRET_WORD";

    final static String EASY_DIFFICULTY = "EASY";
    final static String NORMAL_DIFFICULTY = "NORMAL";
    final static String HARD_DIFFICULTY = "HARD";

    final static String STATE_SECRET_WORD = "SECRET_WORD";
    final static String STATE_WORD_DISPLAY = "WORD_DISPLAY";
    final static String STATE_WRONG_GUESSES = "WRONG_GUESSES";

    String secretWord = "";
    String wordDisplay = "";
    String wrongGuesses = "";

    TextView wordDisplayLabel;
    ImageView gallowsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game_screen);

        registerKeys();
        registerShowKeyboardButton();

        wordDisplayLabel = ( TextView )findViewById( R.id.secretWordLabel );
        gallowsImage = ( ImageView )findViewById( R.id.gallowsImage );

        try {
            actOnIntent();
        } catch ( Exception e ) {
            setSecretWord( "ERROR" );
        }
    }

    @Override
    protected void onSaveInstanceState( Bundle state ) {
        state.putString( STATE_SECRET_WORD, secretWord );
        state.putString( STATE_WORD_DISPLAY, wordDisplay );
        state.putString( STATE_WRONG_GUESSES, wrongGuesses );

    }

    @Override
    protected void onRestoreInstanceState( Bundle state ) {
        secretWord = state.getString( STATE_SECRET_WORD );
        wordDisplay = state.getString( STATE_WORD_DISPLAY );
        wrongGuesses = state.getString( STATE_WRONG_GUESSES );

        updateImage( getNumOfWrongGuesses() );
        wordDisplayLabel.setText( wordDisplay );

        for ( int i = 0; i < wrongGuesses.length(); i++ ) {
            removeFromRemainingText( wrongGuesses.charAt( i ) );
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String keyString = KeyEvent.keyCodeToString( keyCode );

        if ( isKeyStringALetter( keyString ) ) {
            char letter = getLetterFromKeyString( keyString );
            guessLetter( letter );
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
                }
            }
        }
    }

    private void initFromDifficulty( String difficulty ) throws IOException {
        if ( difficulty.equals( EASY_DIFFICULTY ) ) {
            String word = getRandomWord( R.raw.easy, R.raw.easyh );
            setSecretWord( word );
        } else if ( difficulty.equals( NORMAL_DIFFICULTY ) ) {
            String word = getRandomWord( R.raw.normal, R.raw.normalh );
            setSecretWord( word );
        } else if ( difficulty.equals( HARD_DIFFICULTY ) ) {
            String word = getRandomWord( R.raw.hard, R.raw.hardh );
            setSecretWord( word );
        } else {
            setSecretWord( "DERP" );
        }
    }

    private void initFromWord( String word ) {
        setSecretWord( word );
    }

    public void registerButton( int id ) {
        Button button = (Button) findViewById( id );

        if ( button == null ) {
            System.out.println( "WARNING: Couldn't find button to register!" );
            return;
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button b = (Button)v;
                System.out.println( b.getText() );
                boolean right = guessLetter( b.getText().charAt( 0 ) );
                if ( right ) {
                    // 0xAARRGGBB
                    b.setBackgroundColor(0xFF00FF00);
                } else {
                    b.setBackgroundColor(0xFFFF0000);
                    b.setTextColor( 0xffffffff );
                }
            }
        });
    }

    public boolean guessLetter( char letter ) {
        boolean rightGuess;

        if ( !isGuessRight( letter ) ) {
            System.out.println( "WRONG!" );
            addWrongGuess( letter );
            rightGuess = false;
        } else {
            revealLetter( letter );
            rightGuess = true;
        }

        removeFromRemainingText( letter );
        return rightGuess;
    }

    private boolean isGuessRight( char guess ) {
        return secretWord.indexOf( guess ) != -1;
    }

    public void setSecretWord( String word ) {
        word = word.toUpperCase().trim();
        secretWord = word.toUpperCase();
        wordDisplay = "";
        for ( int i = 0; i < word.length(); i++ ) {
            char c = secretWord.charAt( i );
            if ( c == ' ' ) {
                wordDisplay += " ";
            } else {
                wordDisplay += "_";
            }
        }
        wordDisplayLabel.setText( wordDisplay );
        System.out.println( secretWord );
    }

    public void revealLetter( char letter ) {
        char decomposed[] = wordDisplay.toCharArray();

        int spot = secretWord.indexOf( letter );
        while ( spot != -1 ) {
            decomposed[spot] = letter;
            spot = secretWord.indexOf( letter, spot+1 );
        }

        wordDisplay = new String( decomposed );
        wordDisplayLabel.setText( wordDisplay );

        checkWinCondition();
    }

    public void addWrongGuess(char letter) {
        if ( wrongGuesses.indexOf( letter ) != -1 ) return;

        wrongGuesses += letter;

        checkLoseCondition();
        updateImage(getNumOfWrongGuesses());
    }

    public boolean hasWon() {
        return wordDisplay.indexOf( "_" ) == -1;
    }

    public boolean hasLost() {
        return getNumOfWrongGuesses() >= 6;
    }

    public int getNumOfWrongGuesses() {
        return wrongGuesses.length();
    }

    public void checkWinCondition() {
        if ( hasWon() ) {
            showWinScreen();
        }
    }

    public void checkLoseCondition() {
        if ( hasLost() ) {
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
        intent.putExtra( LoseActivity.ANSWER_KEY, secretWord );
        startActivity( intent );
        finish();
    }

    public void updateImage( int wrong ) {
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

    private void registerKeys() {
        /*registerButton( R.id.abutton );
        registerButton( R.id.bbutton );
        registerButton( R.id.button2 );
        registerButton( R.id.button3 );
        registerButton( R.id.button4 );
        registerButton( R.id.button5 );
        registerButton( R.id.button6 );
        registerButton( R.id.button7 );
        registerButton( R.id.button8 );
        registerButton( R.id.button9 );
        registerButton( R.id.button10 );
        registerButton( R.id.button11 );
        registerButton( R.id.button12 );
        registerButton( R.id.button13 );
        registerButton( R.id.button14 );
        registerButton( R.id.button15 );
        registerButton( R.id.button16 );
        registerButton( R.id.button17 );
        registerButton( R.id.button18 );
        registerButton( R.id.button19 );
        registerButton( R.id.button20 );
        registerButton( R.id.button21 );
        registerButton( R.id.button22 );
        registerButton( R.id.button23 );
        registerButton( R.id.button24 );
        registerButton( R.id.button25 );*/
    }

    private String getRandomWord( int fileID, int headerID ) throws IOException {
        int currentLine = 1;
        Random random = new Random();
        String word = "";

        // read number of lines in word file (stored in "header")
        int numLines = readNumLines( headerID );

        // choose a random line using numLines as a bound (+1 because open interval)
        int randomLine = random.nextInt( numLines+1 );

        // open word file
        InputStream input = getResources().openRawResource( fileID );

        // zoom to the line we want, read it, then break
        int c = input.read();
        while ( c != -1 ) {
            if ( currentLine == randomLine ) {
                if ( c == '\n' ) {
                    break;
                } else {
                    word += ( char )c;
                }
            } else {
                if ( c == '\n' ) currentLine++;
            }

            c = input.read();
        }


        input.close();

        return word;
    }

    private int readNumLines( int id ) throws IOException {
        InputStream input = getResources().openRawResource( id );
        String numberString = "";

        int c = input.read();
        while ( c != -1 ) {
            if ( c == '\n' ) {
                break;
            } else {
                numberString += ( char )c;
            }

            c = input.read();
        }

        input.close();

        return Integer.parseInt( numberString );
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

}
