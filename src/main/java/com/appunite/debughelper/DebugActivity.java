package com.appunite.debughelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.debughelper.utils.OptionsDialog;


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
    protected void onDestroy() {
        super.onDestroy();
        DebugHelper.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugHelper.onResume();
    }

    @Override
    public void onSelectOption(SelectOption option) {

    }
}
