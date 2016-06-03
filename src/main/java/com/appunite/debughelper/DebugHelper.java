package com.appunite.debughelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.appunite.debughelper.macro.MacroFragment;
import com.appunite.debughelper.utils.OptionsDialog;
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

    private static Activity currentActivity;
    private static SerialSubscription subscription = new SerialSubscription();
    private static DebugPresenter debugPresenter = null;
    private static ScalpelFrameLayout scalpelFrame;
    private static DebugHelperPreferences debugPreferences;
    private static DebugAdapter debugAdapter;
    private static DisplayMetrics metrics;
    private static ViewGroup mainFrame;
    private static RecyclerView debugRecyclerView;

    public static void setActivity(Activity activity) {
        currentActivity = activity;
        debugPreferences = new DebugHelperPreferences(currentActivity.getApplicationContext());
        debugPresenter = new DebugPresenter(currentActivity);
        if (debugPreferences.getDebugState()) {
            LeakCanary.install(currentActivity.getApplication());
        }
        debugAdapter = new DebugAdapter(debugPreferences);
    }


    @Nonnull
    public static View setContentView(int childId) {
        final View child = currentActivity.getLayoutInflater().inflate(childId, null);
        return setContentView(child);
    }

    @Nonnull
    public static View setContentView(@Nonnull View child) {
        final View root = currentActivity.getLayoutInflater().inflate(R.layout.debug_layout, null);

        mainFrame = (ViewGroup) root.findViewById(R.id.main_frame);
        mainFrame.removeAllViews();
        mainFrame.addView(child);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(currentActivity);
        debugRecyclerView = (RecyclerView) root.findViewById(R.id.debug_drawer);
        debugRecyclerView.setBackgroundColor(Color.parseColor("#cc222222"));
        debugRecyclerView.setLayoutManager(layoutManager);
        debugRecyclerView.setAdapter(debugAdapter);

        return root;
    }

    public static void reSubscribe(final Activity activity) {
        debugPreferences = new DebugHelperPreferences(activity.getApplicationContext());
        debugPresenter = new DebugPresenter(activity);

        final ViewGroup mainView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        final ViewGroup debugView = (ViewGroup) mainView.getChildAt(0);
        scalpelFrame = (ScalpelFrameLayout) debugView.getChildAt(0);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        debugRecyclerView = (RecyclerView) debugView.findViewById(R.id.debug_drawer);
        debugRecyclerView.setBackgroundColor(Color.parseColor("#cc222222"));

        debugRecyclerView.setLayoutManager(layoutManager);
        debugRecyclerView.setAdapter(debugAdapter);

        metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        TinyDancer.create().show(activity.getApplicationContext());
        TinyDancer.hide(activity.getApplicationContext());

        subscription.set(Subscriptions.from(
                debugPresenter.simpleListObservable()
                        .subscribe(debugAdapter),

                Observable.just(activity.getResources().getDisplayMetrics().density * 160f)
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
                                    TinyDancer.create().show(activity.getApplicationContext());
                                } else {
                                    TinyDancer.create().show(activity.getApplicationContext());
                                    TinyDancer.hide(activity.getApplicationContext());
                                }
                            }
                        }),

                debugPresenter.getShowLogObservable()
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LynxConfig lynxConfig = new LynxConfig();
                                lynxConfig.setMaxNumberOfTracesToShow(4000);
                                Intent lynxActivityIntent = LynxActivity.getIntent(activity, lynxConfig);
                                activity.startActivity(lynxActivityIntent);
                            }
                        }),

                debugPresenter.getChangeResponseObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                DebugInterceptor.setEmptyResponse(aBoolean);
                                activity.recreate();
                            }
                        }),

                debugPresenter.showOptionsDialog()
                        .subscribe(new Action1<SelectOption>() {
                            @Override
                            public void call(SelectOption selectOption) {
                                OptionsDialog.newInstance(selectOption).show(activity.getFragmentManager(), null);
                            }
                        }),

                debugPresenter.getShowRequestObservable()
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                activity.getFragmentManager()
                                        .beginTransaction()
                                        .add(InfoListFragment.newInstance(), "REQUEST_COUNTER")
                                        .disallowAddToBackStack()
                                        .commit();
                            }
                        }),
                debugPresenter.getShowMacroObservable()
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                activity.getFragmentManager()
                                        .beginTransaction()
                                        .add(MacroFragment.newInstance(), "MACRO_FRAGMENT")
                                        .disallowAddToBackStack()
                                        .commit();
                            }
                        }),
                debugPresenter.recreateActivityObservable()
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                activity.recreate();
                            }
                        })

        ));
    }

    public static void unSubscribe() {
        subscription.set(Subscriptions.empty());
    }


    @Nonnull
    public static Interceptor getResponseInterceptor() {
        return new SampleInterceptor();
    }

    public static DebugHelperPreferences getDebugPreferences() {
        return debugPreferences;
    }

    public static void updateOption(SelectOption option) {
        switch (option.getOption()) {
            case DebugOption.SET_HTTP_CODE:
                DebugInterceptor.setResponseCode(option.getValues().get(option.getCurrentPosition()));
        }
        debugAdapter.notifyDataSetChanged();
    }
}