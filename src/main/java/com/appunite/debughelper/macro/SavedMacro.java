package com.appunite.debughelper.macro;

public class SavedMacro {

    private final int idView;
    private String text;
    private boolean checked;

    public SavedMacro(int idView, String text) {
        this.idView = idView;
        this.text = text;
    }

    public SavedMacro(int idView, boolean checked) {
        this.idView = idView;
        this.checked = checked;
    }

    public int getIdView() {
        return idView;
    }

    public String getText() {
        return text;
    }

    public boolean isChecked() {
        return checked;
    }
}
