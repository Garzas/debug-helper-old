package com.appunite.debughelper.macro;


import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MacroData<T> {

    private final List<T> adapterList;
    private final TypeToken<List<T>> adapterType;

    public MacroData(List<T> adapterList, TypeToken<List<T>> adapterType) {
        this.adapterList = adapterList;
        this.adapterType = adapterType;
    }

    public List<T> getAdapterList() {
        return adapterList;
    }

    public Type getAdapterType() {
        return adapterType.getType();
    }
}
