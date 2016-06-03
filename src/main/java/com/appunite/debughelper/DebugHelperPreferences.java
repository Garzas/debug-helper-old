package com.appunite.debughelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import javax.annotation.Nonnull;

public class DebugHelperPreferences {

    private static final String DEBUG_DRAWER_PREFS = "debug_drawer_prefs";
    private static final String MOCK_MODE = "debug_helper_mock_mode_status";
    private static final String DEBUG_MACRO = "debug_helper_macro_list";

    @Nonnull
    private final SharedPreferences sharedPreferences;

    public DebugHelperPreferences(@Nonnull Context context) {
        sharedPreferences = context.getSharedPreferences(DEBUG_DRAWER_PREFS, 0);
    }

    public void saveMacroList(String jsonArray) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEBUG_MACRO, jsonArray);
        editor.apply();
    }

    public void saveMockState(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MOCK_MODE, state);
        editor.apply();
    }

    public String getMacroList() {
        return sharedPreferences.getString(DEBUG_MACRO, "[]");
    }

    public boolean getMockState() {
        return sharedPreferences.getBoolean(MOCK_MODE, false);
    }

    public int getMockViewState() {
        boolean mockState = sharedPreferences.getBoolean(MOCK_MODE, false);
        if (mockState) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }
}
