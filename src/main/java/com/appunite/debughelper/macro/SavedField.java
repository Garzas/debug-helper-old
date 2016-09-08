package com.appunite.debughelper.macro;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class SavedField<T> implements GenericSavedField {

    private int idView;
    private String json;
    private Type typeToken;
    private List<T> recyclerViewItems;
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

    public SavedField(int idView, List<T> recyclerViewItems) {
        this.idView = idView;
        this.recyclerViewItems = recyclerViewItems;
    }

    public SavedField(int idView, String json, Type typeToken) {
        this.idView = idView;
        this.json = json;
        this.typeToken = typeToken;
    }

    public SavedField(int idView) {
        this.idView = idView;
    }

    @Nonnull
    public Integer getIdView() {
        return idView;
    }

    public List<T> getRecyclerViewItems() {
        return recyclerViewItems;
    }

    public String getText() {
        return text;
    }

    public Boolean isChecked() {
        return checked;
    }

    public Type getTypeToken() {
        return typeToken;
    }

    public String getJson() {
        return json;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }


    public List<Class<?>> getClasses() {
        final List<Class<?>> classes = new ArrayList<>(recyclerViewItems.size());
        for (Object item : recyclerViewItems) {
            classes.add(item.getClass());
        }
        return classes;
    }

    public Class<?> getClass(final int pos) {
        return recyclerViewItems.get(pos).getClass();
    }
}
