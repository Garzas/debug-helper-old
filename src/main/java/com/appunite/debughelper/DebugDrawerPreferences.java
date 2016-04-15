package com.appunite.debughelper;

import android.content.Context;
import android.content.SharedPreferences;

import javax.annotation.Nonnull;

public class DebugDrawerPreferences {

    private static final String DEBUG_DRAWER_PREFS = "debug_drawer_prefs";
    private static final String LEAK_CANARY_STATE = "cannary_state";

    @Nonnull
    private final SharedPreferences sharedPreferences;

    public DebugDrawerPreferences(@Nonnull Context context) {
        sharedPreferences = context.getSharedPreferences(DEBUG_DRAWER_PREFS, 0);
    }

    public void saveLeakCanaryState(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LEAK_CANARY_STATE, state);
        editor.apply();
    }

    public boolean getLeakCanaryState() {
        return sharedPreferences.getBoolean(LEAK_CANARY_STATE, false);
    }
}
