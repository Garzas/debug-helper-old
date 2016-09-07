package com.appunite.debughelper.macro;

import java.util.List;

public class RecycleViewItem<T> implements GenericSavedField {

    private  final List<T> recyclerViewItems;

    public RecycleViewItem(List<T> recyclerViewItems) {
        this.recyclerViewItems = recyclerViewItems;
    }

    public List<T> getRecyclerViewItems() {
        return recyclerViewItems;
    }
}
