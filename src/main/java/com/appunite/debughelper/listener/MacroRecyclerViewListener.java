package com.appunite.debughelper.listener;

import com.appunite.debughelper.macro.MacroData;

import java.util.List;

public interface MacroRecyclerViewListener<T> {

    MacroData<T> macroData();
    void fillFields(List<T> macroData);

}