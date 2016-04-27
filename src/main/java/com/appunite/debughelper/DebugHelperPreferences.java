package com.appunite.debughelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import javax.annotation.Nonnull;

public class DebugHelperPreferences {

    private static final String DEBUG_DRAWER_PREFS = "debug_drawer_prefs";
    private static final String DEBUG_MODE = "debug_helper_mode_status";
    private static final String MOCK_MODE = "debug_helper_mock_mode_status";

    @Nonnull
    private final SharedPreferences sharedPreferences;

    public DebugHelperPreferences(@Nonnull Context context) {
        sharedPreferences = context.getSharedPreferences(DEBUG_DRAWER_PREFS, 0);
    }


    public void saveDebugState(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DEBUG_MODE, state);
        editor.apply();
    }

    public void saveMockState(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MOCK_MODE, state);
        editor.apply();
    }

    public boolean getDebugState() {
        return sharedPreferences.getBoolean(DEBUG_MODE, true);
    }

    public boolean getMockState() {
        return sharedPreferences.getBoolean(MOCK_MODE, false);
    }

    public int getMockViewState() {
        boolean mockState = sharedPreferences.getBoolean(MOCK_MODE, false);
        if (mockState) {
            return View.VISIBLE;
        }
        else {
            return View.GONE;
        }
    }
}
