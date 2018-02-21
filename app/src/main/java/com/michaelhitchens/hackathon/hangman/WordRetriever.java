package com.michaelhitchens.hackathon.hangman;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Interface for getting words from the word files.
 */
final class WordRetriever {
    /**
     * Get a random word for the given difficulty.
     *
     * @return A random word
     */
    static String getWord(Resources res, Difficulty difficulty) {
        try {
            // Read the number of lines in the word file (stored in the "header").
            int numLines = readNumLines(res, difficulty.getHeaderId());

            // Choose a random line using numLines as a bound.
            // (+1 because Random returns an open interval)
            Random random = new Random();
            int randomLine = random.nextInt(numLines + 1);

            // Open word file
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(res.openRawResource(difficulty.getFileId())));

            // Zoom to the line we want, read it, then return it.
            for (int i = 0; i < randomLine; i++) {
                input.readLine();
            }
            String word = input.readLine();

            input.close();

            return word;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "IOEXCEPTION";
    }

/*******************************************************************************
    PRIVATE INTERFACE
*******************************************************************************/

    /**
     * Read the number of lines from the header file.
     *
     * @param res The app resources manager
     * @param id The header file ID.
     * @return Then number of lines in the corresponding word file.
     * @throws IOException if fail to read the file
     */
    private static int readNumLines(Resources res, int id) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(res.openRawResource(id)));
        String numberString = input.readLine();
        input.close();
        return Integer.parseInt(numberString);
    }
}
