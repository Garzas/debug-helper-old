package com.appunite.debughelper.listener;

import java.util.List;

public interface MacroRecyclerViewListener<T> {

    List<T> macroData();
    void fillFields(List<T> macroData);

}