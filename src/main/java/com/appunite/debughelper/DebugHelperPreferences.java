package com.appunite.debughelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.appunite.debughelper.testpack.AClass;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

public class DebugHelperPreferences {

    private static final String DEBUG_DRAWER_PREFS = "debug_drawer_prefs";
    private static final String MOCK_MODE = "debug_helper_mock_mode_status";
    private static final String DEBUG_MACRO = "debug_helper_macro_list";
    private static final String DEBUG_MACRO_LIST = "debug_macro_list";
    private static final String SPECIAL_DEBUG_MACRO = "debug_helper_macro_list_special";
    private static final String DEBUG_CLASS_TYPES = "debug_helper_class_type_list";

    @Nonnull
    private final SharedPreferences sharedPreferences;

    public DebugHelperPreferences(@Nonnull Context context) {
        sharedPreferences = context.getSharedPreferences(DEBUG_DRAWER_PREFS, 0);
    }

    public void saveMacroList(String json) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEBUG_MACRO, json);
        editor.apply();
    }

    public void saveMacros(List<String> macroList) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final Set<String> set = new HashSet<>();
        set.addAll(macroList);
        editor.putStringSet(DEBUG_MACRO_LIST, set)
                .apply();
    }

    public List<String> getMacrosList() {
        final Set<String> stringSet = sharedPreferences.getStringSet(DEBUG_MACRO_LIST, new HashSet<String>());
        final ArrayList<String> strings = new ArrayList<>();
        strings.addAll(stringSet);
        return strings;
    }


    public void saveMockState(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MOCK_MODE, state);
        editor.apply();
    }

    public void saveSpecialMacro(List list, List<Class> classList) {
        String json = new Gson().toJson(list);
        String classJson = new Gson().toJson(classList);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SPECIAL_DEBUG_MACRO, json);
        editor.putString(DEBUG_CLASS_TYPES, classJson);
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
