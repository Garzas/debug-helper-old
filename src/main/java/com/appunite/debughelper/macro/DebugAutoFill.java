package com.appunite.debughelper.macro;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class DebugAutoFill {

    private final List<SavedMacro> macroList;
    private int hashCode;
    private String macroName;


    public DebugAutoFill(View view, int activityHashCode) {
        hashCode = activityHashCode;
        macroName = "Macro";
        ViewGroup mainView = (ViewGroup) view;
        mainView.getChildCount();
        macroList = createMacro(mainView);
    }

    public List<SavedMacro> createMacro(View view) {
        final List<SavedMacro> macroModelList = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) view;
            final int childCount = parentView.getChildCount();

            for (int i = 0; i < childCount; i++) {
                macroModelList.addAll(createMacro(parentView.getChildAt(i)));
            }

        } else if (view instanceof EditText) {
            EditText editText = (EditText) view;
            macroModelList.add(new SavedMacro(editText.getId(), editText.getText().toString()));
        } else if (view instanceof CompoundButton) {
            CompoundButton button = (CompoundButton) view;
            macroModelList.add(new SavedMacro(button.getId(), button.isChecked()));
        }


        return macroModelList;
    }

    public List<SavedMacro> getMacroList() {
        return macroList;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setMacroName(String name) {
        macroName = name;
    }

    public String getMacroName() {
        return macroName;
    }
}
