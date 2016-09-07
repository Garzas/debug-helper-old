package com.appunite.debughelper.macro;

import javax.annotation.Nonnull;

public class SavedField implements GenericSavedField {

    private int idView;
    private String text;
    private Boolean checked;
    private int selectedPosition;

    public SavedField(int idView, String text) {
        this.idView = idView;
        this.text = text;
    }

    public SavedField(int idView, boolean checked) {
        this.idView = idView;
        this.checked = checked;
    }

    public SavedField(int idView, int selectedPosition) {
        this.idView = idView;
        this.selectedPosition = selectedPosition;
    }

    public SavedField(int idView) {
        this.idView = idView;
    }

    @Nonnull
    public Integer getIdView() {
        return idView;
    }

    public String getText() {
        return text;
    }

    public Boolean isChecked() {
        return checked;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
