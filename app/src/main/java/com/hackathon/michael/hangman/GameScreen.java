package com.hackathon.michael.hangman;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    String secretWord = "";
    String wordDisplay = "";
    String wrongGuesses = "";
    String difficulty = NORMAL_DIFFICULTY;

    TextView wordDisplayLabel;
    ImageView gallowsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game_screen);

        registerKeys();

        wordDisplayLabel = ( TextView )findViewById( R.id.secretWordLabel );
        gallowsImage = ( ImageView )findViewById( R.id.gallowsImage );

        try {
            actOnIntent();
        } catch ( Exception e ) {
            setSecretWord( "ERROR" );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_screen, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
        }

    private void actOnIntent() throws IOException {
        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            difficulty = extras.getString( DIFFICULTY_KEY, null );
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
        this.difficulty = difficulty;
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
                }
            }
        });
    }

    public boolean guessLetter( char letter ) {
        if ( secretWord.indexOf( letter ) == -1 ) {
            System.out.println( "WRONG!" );
            addWrongGuess( letter );
            return false;
        } else {
            revealLetter( letter );
            return true;
        }
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
        updateImage(getNumOfGuesses());
    }

    public boolean hasWon() {
        return wordDisplay.indexOf( "_" ) == -1;
    }

    public boolean hasLost() {
        return getNumOfGuesses() >= 6;
    }

    public int getNumOfGuesses() {
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
            case 0: gallowsImage.setImageResource( R.mipmap.gallows0 ); break;
            case 1: gallowsImage.setImageResource( R.mipmap.gallows1 ); break;
            case 2: gallowsImage.setImageResource( R.mipmap.gallows2 ); break;
            case 3: gallowsImage.setImageResource( R.mipmap.gallows3 ); break;
            case 4: gallowsImage.setImageResource( R.mipmap.gallows4 ); break;
            case 5: gallowsImage.setImageResource( R.mipmap.gallows5 ); break;
            case 6: gallowsImage.setImageResource( R.mipmap.gallows6 ); break;
        }
    }

    private void registerKeys() {
        registerButton( R.id.abutton );
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
        registerButton( R.id.button25 );
    }

    private String getRandomWord( int id ) throws IOException {
        InputStream input = getResources().openRawResource( id );
        ArrayList<String> list = new ArrayList<>();
        String word = "";

        int c = input.read();
        while ( c != -1 ) {
            if ( c == '\n' ) {
                list.add( word );
                word = "";
            } else {
                word += ( char )c;
            }

            c = input.read();
        }

        input.close();

        Random random = new Random();
        int randomInt = random.nextInt( list.size() );
        return list.get( randomInt );
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
}
