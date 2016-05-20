package com.appunite.debughelper.macro;

import java.util.List;

public class MacroItem {

    private List<SavedMacro> macroList;
    private int hashCode;
    private String macroName;


    public MacroItem(List<SavedMacro> macroList, int activityHashCode) {
        this.macroList = macroList;
        hashCode = activityHashCode;
        macroName = "Macro";
    }

    public List<SavedMacro> getMacroList() {
        return macroList;
    }

    public int getHashCode() {
        return hashCode;
    }

    public String getMacroName() {
        return macroName;
    }
}
