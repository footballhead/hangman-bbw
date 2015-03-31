package com.michaelhitchens.hackathon.hangman;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class CustomWordActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_custom_word);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Button button = ( Button )findViewById( R.id.confirmButton );
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        EditText input = (EditText)findViewById( R.id.customWordText );
        String word = formatInput( input.getText().toString() );
        if ( word.length() > 0 ) {
            startGameWithWord( word );
        }

    }

    private String formatInput( String text ) {
        String formatted = "";
        String trimmed = text.trim();

        for ( int i = 0; i < trimmed.length(); i++ ) {
            char c = trimmed.charAt( i );
            if ( isLetter( c ) ) {
                formatted += toUpperCase( c );
            } else if ( c == ' ' ) {
                formatted += ' ';
            }
        }

        return formatted;
    }

    private boolean isLetter( char c ) {
        return isLowerCaseLetter( c ) || isUpperCaseLetter( c );
    }

    private boolean isUpperCaseLetter( char c ) {
        return ( c >= 'A' && c <= 'Z' );
    }

    private boolean isLowerCaseLetter( char c ) {
        return ( c >= 'a' && c <= 'z' );
    }

    private char toUpperCase( char c ) {
        if ( isUpperCaseLetter( c ) ) return c;

        char translate = 'a' - 'A';
        return ( char )( c - translate );
    }

    private void startGameWithWord( String word ) {
        Intent intent = new Intent( this, GameScreen.class );
        intent.putExtra( GameScreen.CUSTOM_WORD_KEY, word );
        startActivity( intent );
        finish();
    }
}
