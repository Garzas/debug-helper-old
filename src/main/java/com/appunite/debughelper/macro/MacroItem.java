package com.appunite.debughelper.macro;

import java.util.ArrayList;
import java.util.List;

public class MacroItem<T extends GenericSavedField> {

    private List<T> baseFieldItems;
    private final int hashCode;
    private String macroName;


    public MacroItem(List<T> baseFieldItems, int activityHashCode) {
        this.baseFieldItems = baseFieldItems;
        hashCode = activityHashCode;
        macroName = "Macro";
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

    public List<T> getBaseFieldItems() {
        return baseFieldItems;
    }

    public List<Class<?>> getClasses() {
        final List<Class<?>> classes = new ArrayList<>(baseFieldItems.size());
        for (Object item : baseFieldItems) {
            classes.add(item.getClass());
        }
        return classes;
    }
}
