package com.michaelhitchens.hackathon.hangman;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;


public class LoseActivity extends ActionBarActivity {
    final public static String ANSWER_KEY = "ANSWER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_lose);

        if ( getResources().getBoolean(R.bool.portrait_only) ) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Button b = ( Button )findViewById( R.id.mainMenuButton );
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        displayAnswer();
    }

    private void displayAnswer() {
        Bundle extra = getIntent().getExtras();
        if ( extra != null ) {
            String answer = extra.getString( ANSWER_KEY, null );
            if ( answer != null ) {
                TextView answerText = (TextView)findViewById( R.id.answerText );
                answerText.setText( answer );
            }
        }
    }
}
