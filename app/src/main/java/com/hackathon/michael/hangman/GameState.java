package com.hackathon.michael.hangman;

import android.os.Bundle;

/**
 * Holds the secret word and handles guessing.
 */
public class GameState {
    /** The state key used to store the secret word */
    final private static String STATE_SECRET_WORD = "SECRET_WORD";
    /** The state key used to store the wrong guesses the user made */
    final private static String STATE_WRONG_GUESSES = "WRONG_GUESSES";
    /** The state key used to store the right guesses the user made */
    final private static String STATE_RIGHT_GUESSES = "RIGHT_GUESSES";

    /** The word the user is trying to guess. */
    private String secretWord;
    /** The obfuscated word the user sees as they guess. */
    private String wordDisplay;

    /** The letters that the user guessed which have been wrong. */
    private String wrongGuesses = "";
    /** The letters that the user guessed which have been right. */
    private String rightGuesses = "";

/*******************************************************************************
        PUBLIC INTERFACE
*******************************************************************************/

    /**
     * Start a new game with a given word to guess and initialize state.
     *
     * @param word The word the user is trying to guess.
     */
    public GameState( String word ) {
        word = word.toUpperCase().trim();

        secretWord = word;
        initBlankDisplay();

        System.out.println( secretWord ); // DEBUG!!!
    }

    /**
     * Guess a letter in the secret word.
     *
     * @param letter The letter to guess (from the English alphabet)
     * @return `true` if guess is right and reveals new characters, `false`
     *         otherwise.
     */
    public boolean guess( char letter ) {
        if ( isAlreadyGuessed( letter ) ) return false;

        boolean rightGuess = true;

        if ( isGuessRight( letter ) ) {
            addRightGuess( letter );
            reveal( letter );
        } else {
            addWrongGuess( letter );
            rightGuess = false;
        }

        return rightGuess;
    }

    /**
     * Get whether the player has won.
     *
     * @return `true` if won, `false` otherwise.
     */
    public boolean hasWon() {
        return wordDisplay.indexOf( "_" ) == -1;
    }

    /**
     * Get whether the player has lost.
     *
     * @return `true` if lost, `false` otherwise.
     */
    public boolean hasLost() {
        return getNumOfWrongGuesses() >= 6;
    }

    /**
     * Get the number of wrong guesses the player has made.
     *
     * @return The number of wrong guesses.
     */
    public int getNumOfWrongGuesses() {
        return wrongGuesses.length();
    }

    /**
     * Get whether a letter has already been guessed.
     *
     * @return `true` if guessed, `false` otherwise
     */
    public boolean isAlreadyGuessed( char letter ) {
        return rightGuesses.indexOf( letter ) != -1 ||
                wrongGuesses.indexOf( letter ) != -1;
    }

    /**
     * Get the secret word but ONLY AFTER YOU LOSE!
     *
     * @return The secret word when you lose, or `null` otherwise.
     */
    public String getSecretWordOnLoss() {
        return ( hasLost() ? secretWord : null );
    }


    /**
     * Get the obfuscated word display.
     *
     * @return The obfuscated word display
     */
    public String getDisplay() {
        return wordDisplay;
    }

    /**
     * Get a string containing all the wrong guesses the user has made
     *
     * @return All wrong guesses
     */
    public String getWrongGuesses() {
        return wrongGuesses;
    }

    /**
     * Get a string containing all the right guesses the user has made
     *
     * @return All right guesses
     */
    public String getRightGuesses() {
        return rightGuesses;
    }

    /**
     * Save the game state to an external dictionary.
     *
     * This is primarily available for the `onSaveInstanceState` of Android
     * activities.
     *
     * @param state The dictionary to store the state in.
     */
    public void saveState( Bundle state ) {
        state.putString( STATE_SECRET_WORD, secretWord );
        state.putString( STATE_WRONG_GUESSES, wrongGuesses );
        state.putString( STATE_RIGHT_GUESSES, rightGuesses );
    }

    /**
     * Load the game state from a dictionary to resume play.
     *
     * We must save the state first to the given bundle in order to load it.
     *
     * This is available for the `onRestoreInstanceState` of Android activities.
     *
     * @param state The dictionary to load state from.
     */
    public void loadState( Bundle state ) {
        secretWord = state.getString( STATE_SECRET_WORD );
        wrongGuesses = state.getString( STATE_WRONG_GUESSES );
        rightGuesses = state.getString( STATE_RIGHT_GUESSES );

        rebuildDisplay();
    }

/*******************************************************************************
    PRIVATE INTERFACE
*******************************************************************************/

    /**
     * Check whether the given guessed letter is in the secret word.
     *
     * @param letter The letter to guess.
     * @return `true` if guess is right, `false` if wrong
     */
    private boolean isGuessRight( char letter ) {
        return secretWord.indexOf( letter ) != -1;
    }

    /**
     * Update the word display to reveal all occurrences of the given letter.
     *
     * The assumption is that the letter is a right guess.
     *
     * @param letter The letter to reveal.
     */
    private void reveal( char letter ) {
        char decomposed[] = wordDisplay.toCharArray();

        int spot = secretWord.indexOf( letter );
        while ( spot != -1 ) {
            decomposed[spot] = letter;
            spot = secretWord.indexOf( letter, spot+1 );
        }

        wordDisplay = new String( decomposed );
    }

    /**
     * Add a letter to the wrong guess list.
     *
     * @param letter The wrong guess to register.
     */
    private void addWrongGuess( char letter ) {
        if ( wrongGuesses.indexOf( letter ) != -1 ) return;

        wrongGuesses += letter;
    }

    /**
     * Add a letter to the right guess list.
     *
     * @param letter The right guess to register.
     */
    private void addRightGuess( char letter ) {
        if ( rightGuesses.indexOf( letter ) != -1 ) return;

        rightGuesses += letter;
    }

    /**
     * Rebuild the word display.
     *
     * This assumes `rightGuesses` is populated.
     */
    private void rebuildDisplay() {
        for ( int i = 0; i < rightGuesses.length(); i++ ) {
            reveal( rightGuesses.charAt( i ) );
        }
    }

    /**
     * Initialize the word display and set it blank.
     */
    private void initBlankDisplay() {
        wordDisplay = "";

        for (int i = 0; i < secretWord.length(); i++) {
            char c = secretWord.charAt(i);

            wordDisplay += ( c == ' ' ? " " : "_" );
        }
    }

}
