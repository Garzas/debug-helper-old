package com.appunite.debughelper.macro;

import java.util.List;

public class MacroItem {

    private List<SavedField> macroList;
    private int hashCode;
    private String macroName;


    public MacroItem(List<SavedField> macroList, int activityHashCode) {
        this.macroList = macroList;
        hashCode = activityHashCode;
        macroName = "Macro";
    }

    public List<SavedField> getMacroList() {
        return macroList;
    }

    public int getHashCode() {
        return hashCode;
    }

    public String getMacroName() {
        return macroName;
    }

    public void setMacroName(String macroName) {
        this.macroName = macroName;
    }
}
