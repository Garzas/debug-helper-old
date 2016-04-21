package com.appunite.debughelper;


import android.content.Context;
import android.os.Build;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func4;
import rx.observers.Observers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class DebugPresenter {

    @Nonnull
    private final PublishSubject<SelectOption> selectSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<SwitchOption> optionSubject = PublishSubject.create();
    @Nonnull
    private final Observable<Boolean> scalpelObservable;
    @Nonnull
    private final Observable<List<BaseDebugItem>> scalpelUtilsList;
    @Nonnull
    private final Observable<Boolean> drawViewsObservable;
    @Nonnull
    private final Observable<Boolean> showIdObservable;
    @Nonnull
    private final Observable<List<BaseDebugItem>> utilList;
    @Nonnull
    private final Observable<Boolean> fpsLabelObservable;
    @Nonnull
    private final PublishSubject<Integer> actionSubject = PublishSubject.create();
    @Nonnull
    private final Observable<String> showLogObservable;
    @Nonnull
    private final Observable<Boolean> leakCanaryObservable;
    @Nonnull
    private final BehaviorSubject<List<BaseDebugItem>> simpleListSubject = BehaviorSubject.create();
    @Nonnull
    private final Observable<List<InformationItem>> deviceInfoList;
    @Nonnull
    private final Observable<List<InformationItem>> buildInfoList;
    @Nonnull
    private final PublishSubject<Float> densitySubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> resolutionSubject = PublishSubject.create();
    @Nonnull
    private final Context context;
    @Nonnull
    private final Observable<Integer> delayObservable;
    @Nonnull
    private final Observable<Integer> httpCodeObservable;


    public abstract static class BaseDebugItem {
    }

    public class CategoryItem extends BaseDebugItem {

        @Nonnull
        private final String title;

        public CategoryItem(@Nonnull String title) {
            this.title = title;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                }
            });
        }

    }

    public class InformationItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        @Nonnull
        private final String value;

        public InformationItem(@Nonnull String name, @Nonnull String value) {
            this.name = name;
            this.value = value;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof InformationItem)) {
                return false;
            }
            InformationItem that = (InformationItem) o;
            return Objects.equal(name, that.name)
                    && Objects.equal(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, value);
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                }
            });
        }
    }

    public class SpinnerItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        @Nonnull
        private final int option;
        @Nonnull
        private final List<Integer> values;

        public SpinnerItem(@Nonnull String name, @Nonnull int option, @Nonnull List<Integer> values) {
            this.name = name;
            this.option = option;
            this.values = values;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public List<Integer> getValues() {
            return values;
        }

        @Nonnull
        public int getOption() {
            return option;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SpinnerItem)) {
                return false;
            }
            SpinnerItem that = (SpinnerItem) o;
            return Objects.equal(name, that.name)
                    && Objects.equal(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, values);
        }

        public Observer<Integer> clickObserver() {
            return Observers.create(new Action1<Integer>() {
                @Override
                public void call(Integer value) {
                    selectSubject.onNext(new SelectOption(value, option));
                }
            });
        }
    }

    public class SwitchItem extends BaseDebugItem {

        @Nonnull
        private final String title;
        private int option;

        public SwitchItem(@Nonnull String title, int option) {
            this.title = title;
            this.option = option;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

        public int getOption() {
            return option;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SwitchItem)) {
                return false;
            }
            SwitchItem that = (SwitchItem) o;
            return Objects.equal(title, that.title)
                    && Objects.equal(option, that.option);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(title, option);
        }

        public Observer<Boolean> switchOption() {
            return Observers.create(new Action1<Boolean>() {
                @Override
                public void call(Boolean set) {
                    optionSubject.onNext(new SwitchOption(set, option));
                }
            });
        }
    }

    public class ActionItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        private int action;

        public ActionItem(@Nonnull String name, int action) {
            this.name = name;
            this.action = action;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        public int getAction() {
            return action;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ActionItem)) {
                return false;
            }
            ActionItem that = (ActionItem) o;
            return Objects.equal(name, that.name)
                    && Objects.equal(action, that.action);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, action);
        }

        public Observer<Object> actionOption() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    actionSubject.onNext(action);
                }
            });
        }
    }

    public DebugPresenter(final Context context) {
        this.context = context;

        deviceInfoList = Observable.combineLatest(
                resolutionSubject,
                densitySubject,
                new Func2<String, Float, List<InformationItem>>() {
                    @Override
                    public List<InformationItem> call(String resolution, Float density) {
                        return ImmutableList.of(
                                new InformationItem("Model", Build.MANUFACTURER + " " + Build.MODEL),
                                new InformationItem("SDK", DebugTools.checkSDKNamme(
                                        Build.VERSION.SDK_INT)
                                        + "(" + Build.VERSION.SDK_INT
                                        + " API)"),
                                new InformationItem("Release", Build.VERSION.RELEASE),
                                new InformationItem("Resolution", resolution),
                                new InformationItem("Density", Math.round(density) + "dpi"));
                    }
                });

        buildInfoList = densitySubject
                .map(new Func1<Float, List<InformationItem>>() {
                    @Override
                    public List<InformationItem> call(Float density) {
                        return ImmutableList.of(
                                new InformationItem("Name", DebugTools.getApplicationName(context)),
                                new InformationItem("Package", context.getPackageName()),
                                new InformationItem("Build Type", DebugTools.getBuildType(context)),
                                new InformationItem("Version", DebugTools.getBuildVersion(context))
                        );

                    }
                });

        scalpelUtilsList = Observable.just(new Object())
                .map(new Func1<Object, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(Object o) {
                        return ImmutableList.<BaseDebugItem>of(
                                new SwitchItem("Turn Scalpel ", DebugOption.SET_SCALPEL),
                                new SwitchItem("Draw Views", DebugOption.SCALPEL_DRAW_VIEWS),
                                new SwitchItem("Show Ids", DebugOption.SCALPEL_SHOW_ID));
                    }
                });

        utilList = Observable.just(new Object())
                .map(new Func1<Object, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(Object o) {
                        return ImmutableList.<BaseDebugItem>of(
                                new SwitchItem("FPS Label", DebugOption.FPS_LABEL),
                                new SwitchItem("LeakCanary", DebugOption.LEAK_CANARY),
                                new ActionItem("Show Log", DebugOption.SHOW_LOG));
                    }
                });


        Observable.combineLatest(
                deviceInfoList,
                buildInfoList,
                scalpelUtilsList,
                utilList,
                new Func4<List<InformationItem>, List<InformationItem>, List<BaseDebugItem>,
                        List<BaseDebugItem>, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(
                            List<InformationItem> deviceInfo,
                            List<InformationItem> buildInfo,
                            List<BaseDebugItem> scalpelUtils,
                            List<BaseDebugItem> utils) {
                        return ImmutableList.<BaseDebugItem>builder()
                                .add(new CategoryItem("Device Information"))
                                .addAll(deviceInfo)
                                .add(new CategoryItem("About app"))
                                .addAll(buildInfo)
                                .add(new CategoryItem("OKHTTP options"))
                                .add(new SpinnerItem("Http code", DebugOption.SET_HTTP_CODE,
                                        ImmutableList.of(200,
                                                201, 202, 203, 204, 205,
                                                206, 300, 301, 302, 303,
                                                304, 305, 400, 401, 402,
                                                403, 404, 405, 406, 407,
                                                408, 409, 410, 411, 412,
                                                413, 414, 415, 500, 501,
                                                502, 503, 504, 505)))
                                .add(new SpinnerItem("Delay[ms]", DebugOption.SET_DELAY, ImmutableList.of(100, 500, 1000, 2000, 10000)))
                                .add(new CategoryItem("Scalpel Utils"))
                                .addAll(scalpelUtils)
                                .add(new CategoryItem("Tools"))
                                .addAll(utils)
                                .build();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simpleListSubject);


        scalpelObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.SET_SCALPEL;
                    }
                })
                .map(checkSet());

        drawViewsObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.SCALPEL_DRAW_VIEWS;
                    }
                })
                .map(checkSet());

        showIdObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.SCALPEL_SHOW_ID;
                    }
                })
                .map(checkSet());

        fpsLabelObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.FPS_LABEL;
                    }
                })
                .map(checkSet());

        leakCanaryObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.LEAK_CANARY;
                    }
                })
                .map(checkSet());

        showLogObservable = actionSubject
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer.equals(DebugOption.SHOW_LOG);
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "d";
                    }
                });

        delayObservable = selectSubject
                .filter(new Func1<SelectOption, Boolean>() {
                    @Override
                    public Boolean call(SelectOption selectOption) {
                        return selectOption.getOption() == DebugOption.SET_DELAY;
                    }
                })
//                .distinctUntilChanged()
                .map(selectValue());

        httpCodeObservable = selectSubject
                .filter(new Func1<SelectOption, Boolean>() {
                    @Override
                    public Boolean call(SelectOption selectOption) {
                        return selectOption.getOption() == DebugOption.SET_HTTP_CODE;
                    }
                })
                .map(selectValue());


    }

    private Func1<SelectOption, Integer> selectValue() {
        return new Func1<SelectOption, Integer>() {
            @Override
            public Integer call(SelectOption selectOption) {
                return selectOption.getValue();
            }
        };
    }

    @Nonnull
    private Func1<SwitchOption, Boolean> checkSet() {
        return new Func1<SwitchOption, Boolean>() {
            @Override
            public Boolean call(SwitchOption switchOption) {
                return switchOption.isSet();
            }
        };
    }


    @Nonnull
    public Observable<List<BaseDebugItem>> simpleListObservable() {
        return simpleListSubject;
    }

    @Nonnull
    public Observer<Float> densityObserver() {
        return densitySubject;
    }

    @Nonnull
    public Observable<Integer> getDelayObservable() {
        return delayObservable;
    }

    @Nonnull
    public Observer<String> resolutionObserver() {
        return resolutionSubject;
    }

    @Nonnull
    public Observable<Boolean> getFpsLabelObservable() {
        return fpsLabelObservable;
    }

    @Nonnull
    public Observable<String> getShowLogObservable() {
        return showLogObservable;
    }

    @Nonnull
    public Observable<Boolean> getLeakCanaryObservable() {
        return leakCanaryObservable;
    }

    @Nonnull
    public Observable<Boolean> setScalpelObservable() {
        return scalpelObservable;
    }

    @Nonnull
    public Observable<Boolean> setDrawViewsObservable() {
        return drawViewsObservable;
    }

    @Nonnull
    public Observable<Boolean> setShowIdsObservable() {
        return showIdObservable;
    }

    @Nonnull
    public Observable<Integer> getHttpCodeObservable() {
        return httpCodeObservable;
    }
}
