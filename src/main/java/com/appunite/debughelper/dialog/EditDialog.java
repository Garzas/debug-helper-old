package com.appunite.debughelper.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import javax.annotation.Nonnull;


public class EditDialog extends DialogFragment {

    private static int currentItem;
    private static String currentName;

    public EditDialog() {
    }

    public interface OnChangeNameListener {
        void onChangeName(int position, String newName);
    }

    public static EditDialog newInstance(int position, String name) {
        currentItem = position;
        currentName = name;
        return new EditDialog();
    }

    @Nonnull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText editText = new EditText(getActivity());
        editText.setText(currentName);

        return new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog)
                .setView(editText)
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
                        OnChangeNameListener listener = (OnChangeNameListener) getTargetFragment();
                        listener.onChangeName(currentItem, editText.getText().toString());
                    }
                })
                .create();
    }

}