package com.appunite.debughelper.macro;

import javax.annotation.Nonnull;

public class SavedField {

    private Integer idView;
    private String text;
    private Boolean checked;

    public SavedField(Integer idView, String text) {
        this.idView = idView;
        this.text = text;
    }

    public SavedField(int idView, boolean checked) {
        this.idView = idView;
        this.checked = checked;
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
}
