package com.appunite.debughelper.macro;

import java.util.ArrayList;
import java.util.List;

public class MacroItem<T extends GenericSavedField> {

    private List<T> baseFieldItems;
    private final String activityName;
    private String macroName;


    public MacroItem(final List<T> baseFieldItems, final String activityName) {
        this.baseFieldItems = baseFieldItems;
        this.activityName = activityName;
        macroName = activityName + " Macro";
    }

    public String getActivityName() {
        return activityName;
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
