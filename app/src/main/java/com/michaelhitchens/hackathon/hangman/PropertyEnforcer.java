package com.michaelhitchens.hackathon.hangman;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

final class PropertyEnforcer {
    static void enforceOrientation(AppCompatActivity activity) {
        if (activity.getResources().getBoolean(R.bool.portrait_only)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (activity.getResources().getBoolean(R.bool.landscape_only)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    static void enforceSoftKeyboard(AppCompatActivity activity) {
        if (!activity.getResources().getBoolean(R.bool.force_keyboard)) {
            return;
        }

        InputMethodManager inputMethodManager =
                (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) {
            System.out.println("Could not get input manager! OH NO!");
            return;
        }

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
