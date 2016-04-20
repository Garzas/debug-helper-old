package com.appunite.debughelper;

public class SelectOption {

    private final int value;
    private final int option;

    public SelectOption(int value, int option) {
        this.value = value;
        this.option = option;
    }

    public int getValue() {
        return value;
    }

    public int getOption() {
        return option;
    }
}
