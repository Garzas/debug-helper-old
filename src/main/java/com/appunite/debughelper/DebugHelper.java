package com.appunite.debughelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.github.pedrovgs.lynx.LynxActivity;
import com.github.pedrovgs.lynx.LynxConfig;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.squareup.leakcanary.LeakCanary;

import javax.annotation.Nonnull;

import okhttp3.Interceptor;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class DebugHelper {


    public Boolean isWorking() {
        return true;
    }

    private static Activity mActivity;
    private static SerialSubscription subscription = new SerialSubscription();
    private static DebugPresenter debugPresenter = null;
    static DebugHelperPreferences debugPreferences;

    public static void setActivity(Activity activity) {
        mActivity = activity;
        debugPreferences = new DebugHelperPreferences(mActivity.getApplicationContext());
        debugPresenter = new DebugPresenter(mActivity);
        if (debugPreferences.getLeakCanaryState()) {
            LeakCanary.install(mActivity.getApplication());
        }
    }


    @Nonnull
    public static View setContentView(int childId) {
        final View child = mActivity.getLayoutInflater().inflate(childId, null);
        return setContentView(child);
    }

    @Nonnull
    public static View setContentView(@Nonnull View child) {
        final View root = mActivity.getLayoutInflater().inflate(R.layout.debug_layout, null);
        final ViewGroup mainFrame = (ViewGroup) root.findViewById(R.id.main_frame);
        mainFrame.removeAllViews();
        mainFrame.addView(child);
        final ScalpelFrameLayout scalpelFrame = (ScalpelFrameLayout) mainFrame;
        final DebugAdapter debugAdapter = new DebugAdapter(debugPreferences);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);

        RecyclerView debugRecyclerView = (RecyclerView) root.findViewById(R.id.debug_drawer);
        debugRecyclerView.setBackgroundColor(Color.parseColor("#cc222222"));

        debugRecyclerView.setLayoutManager(layoutManager);
        debugRecyclerView.setAdapter(debugAdapter);

        final DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        subscription.set(Subscriptions.from(
                debugPresenter.simpleListObservable()
                        .subscribe(debugAdapter),

                Observable.just(mActivity.getResources().getDisplayMetrics().density * 160f)
                        .subscribe(debugPresenter.densityObserver()),

                Observable.just(metrics.widthPixels + "x" + metrics.heightPixels)
                        .subscribe(debugPresenter.resolutionObserver()),

                debugPresenter.setScalpelObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setLayerInteractionEnabled(aBoolean);
                            }
                        }),

                debugPresenter.setDrawViewsObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setDrawViews(!aBoolean);
                            }
                        }),

                debugPresenter.setShowIdsObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setDrawIds(aBoolean);
                            }
                        }),

                debugPresenter.getFpsLabelObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean isSet) {
                                if (isSet) {
                                    TinyDancer.create().show(mActivity.getApplicationContext());
                                } else {
                                    TinyDancer.create().show(mActivity.getApplicationContext());
                                    TinyDancer.hide(mActivity.getApplicationContext());
                                }
                            }
                        }),

                debugPresenter.getShowLogObservable()
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LynxConfig lynxConfig = new LynxConfig();
                                lynxConfig.setMaxNumberOfTracesToShow(4000);
                                Intent lynxActivityIntent = LynxActivity.getIntent(mActivity, lynxConfig);
                                mActivity.startActivity(lynxActivityIntent);
                            }
                        }),

                debugPresenter.getLeakCanaryObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean isSet) {
                                debugPreferences.saveLeakCanaryState(isSet);
                                mActivity.recreate();
                                //TODO disable leakcannary for all activities
                            }
                        }),

                debugPresenter.getHttpCodeObservable()
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer code) {
                                ResponseInterceptor.setResponseCode(code);
                                mActivity.recreate();
                            }
                        })

        ));

        return root;
    }

    public static void onDestroy() {
        subscription.set(Subscriptions.empty());
    }

    public static void onResume() {
        TinyDancer.create().show(mActivity.getApplicationContext());
        TinyDancer.hide(mActivity.getApplicationContext());
    }

    @Nonnull
    public static Interceptor getDelayInterceptor() {
        return new ResponseInterceptor();
    }

}