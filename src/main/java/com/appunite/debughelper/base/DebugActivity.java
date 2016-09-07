package com.appunite.debughelper.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.debughelper.DebugHelper;
import com.appunite.debughelper.model.SelectOption;
import com.appunite.debughelper.dialog.OptionsDialog;
import com.appunite.debughelper.utils.DebugPermissions;

import javax.annotation.Nonnull;

public abstract class DebugActivity extends AppCompatActivity implements OptionsDialog.OnSelectOptionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugHelper.setActivity(this);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(DebugHelper.setContentView(layoutResID));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(DebugHelper.setContentView(view));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(DebugHelper.setContentView(view), params);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @Nonnull final String permissions[], @Nonnull final int[] grantResults) {
        switch (requestCode) {
            case DebugPermissions.SCREEN_ABOVE_OTHERS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugHelper.unSubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugHelper.reSubscribe(this);
    }

    @Override
    public void onSelectOption(SelectOption option) {
        DebugHelper.updateOption(option);
    }
}
