package com.appunite.debughelper;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.example.debugutilsexample.dagger.BaseActivityComponent;
import com.appunite.example.debugutilsexample.dagger.BaseActivityComponentProvider;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import javax.annotation.Nonnull;


public abstract class RxDebugActivity extends RxAppCompatActivity implements BaseActivityComponentProvider {

    private BaseActivityComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityComponent = createActivityComponent(savedInstanceState);
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

    @Nonnull
    public BaseActivityComponent getActivityComponent() {
        return activityComponent;
    }
}
