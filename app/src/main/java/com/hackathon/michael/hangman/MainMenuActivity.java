package com.hackathon.michael.hangman;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainMenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_menu);

        registerButton( R.id.easyButton, GameScreen.EASY_DIFFICULTY );
        registerButton( R.id.normalButton, GameScreen.NORMAL_DIFFICULTY );
        registerButton( R.id.hardButton, GameScreen.HARD_DIFFICULTY );

        Button button = ( Button )findViewById( R.id.customButton );
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gotoWordEnterScreen();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

    public void registerButton( int id, final String difficulty ) {
        Button button = ( Button )findViewById( id );
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startGame( difficulty );
            }
        });
    }

    public void startGame( String difficulty ) {
        Intent intent = new Intent( this, GameScreen.class );
        intent.putExtra( GameScreen.DIFFICULTY_KEY, difficulty );
        startActivity( intent );
    }

    public void gotoWordEnterScreen() {
        Intent intent = new Intent( this, CustomWordActivity.class );
        startActivity( intent );
    }
}
