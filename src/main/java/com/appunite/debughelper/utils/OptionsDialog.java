package com.appunite.debughelper.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.appunite.debughelper.SelectOption;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class OptionsDialog extends DialogFragment {

    private static int currentItem;
    private static SelectOption selectOption;

    public OptionsDialog() {


    }

    public interface OnSelectOptionListener {

        void onSelectOption(SelectOption option);

    }

    public static OptionsDialog newInstance(SelectOption option) {
        currentItem = option.getCurrentPosition();
        selectOption = option;
        return new OptionsDialog();
    }

    @Nonnull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        List<Integer> list = ImmutableList.of(200,
//                201, 202, 203, 204, 205,
//                206, 300, 301, 302, 303,
//                304, 305, 400, 401, 402,
//                403, 404, 405, 406, 407,
//                408, 409, 410, 411, 412,
//                413, 414, 415, 500, 501,
//                502, 503, 504, 505);

        final String[] strings = Iterables.toArray(Iterables.transform(selectOption.getValues(), new Function<Integer, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Integer input) {
                return input.toString();
            }
        }), String.class);


        return new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog)
                .setSingleChoiceItems(strings, selectOption.getCurrentPosition(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentItem = which;
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        OnSelectOptionListener listener = (OnSelectOptionListener) getActivity();
                        listener.onSelectOption(selectOption);
                    }
                })
                .create();
    }

}