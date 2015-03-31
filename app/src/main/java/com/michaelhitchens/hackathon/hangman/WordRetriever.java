package com.michaelhitchens.hackathon.hangman;


import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Interface for getting words from the word files.
 */
public class WordRetriever {
    /** The Resources instance we use to read files */
    private static Resources res;

/*******************************************************************************
    PUBLIC INTERFACE
*******************************************************************************/

    /**
     * Register the Resources object we'll use to read files.
     *
     * @param res The app Resources object.
     */
    public static void setResources( Resources res ) {
        WordRetriever.res = res;
    }

    /**
     * Get a random easy word.
     *
     * @return A random easy word
     * @throws IOException if fail to read word files
     */
    public static String getEasyWord() throws IOException {
        return getRandomWord( R.raw.easy, R.raw.easyh );
    }

    /**
     * Get a random normal word.
     *
     * @return A random normal word
     * @throws IOException if fail to read word files
     */
    public static String getNormalWord() throws IOException {
        return getRandomWord( R.raw.normal, R.raw.normalh );
    }

    /**
     * Get a random hard word.
     *
     * @return A random hard word
     * @throws IOException if fail to read word files
     */
    public static String getHardWord() throws IOException {
        return getRandomWord( R.raw.hard, R.raw.hardh );
    }

/*******************************************************************************
    PRIVATE INTERFACE
*******************************************************************************/

    /**
     * Get a random word from a word file.
     *
     * @param fileID The file with one word per line.
     * @param headerID The file with the number of lines in `fileID`.
     * @return A random word from the word file.
     * @throws IOException if fail to read word files
     */
    private static String getRandomWord( int fileID, int headerID )
            throws IOException
    {
        int currentLine = 1;
        Random random = new Random();
        String word = "";

        // read number of lines in word file (stored in "header")
        int numLines = readNumLines( headerID );

        // choose a random line using numLines as a bound (+1 because open interval)
        int randomLine = random.nextInt( numLines+1 );

        // open word file
        InputStream input = res.openRawResource( fileID );

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

    /**
     * Read the number of lines from the header file.
     *
     * @param id The header file ID.
     * @return Then number of lines in the corresponding word file.
     * @throws IOException if fail to read the file
     */
    private static int readNumLines( int id ) throws IOException {
        InputStream input = res.openRawResource( id );
        String numberString = "";

        int c;
        while ( ( c = input.read() ) != -1 ) {
            if ( c == '\n' ) {
                break;
            } else {
                numberString += ( char )c;
            }
        }

        input.close();

        return Integer.parseInt( numberString );
    }
}
