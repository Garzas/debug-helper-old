package com.appunite.debughelper.macro;

import javax.annotation.Nonnull;

public class SavedMacro {

    private Integer idView;
    private String text;
    private Boolean checked;

    public SavedMacro(Integer idView, String text) {
        this.idView = idView;
        this.text = text;
    }

    public SavedMacro(int idView, boolean checked) {
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
