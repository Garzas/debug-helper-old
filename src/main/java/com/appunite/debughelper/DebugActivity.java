package com.appunite.debughelper;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;


public abstract class DebugActivity extends RxAppCompatActivity {

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

}
