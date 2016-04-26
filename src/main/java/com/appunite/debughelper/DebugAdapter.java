package com.appunite.debughelper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import java.util.List;


import javax.annotation.Nonnull;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

abstract class BaseDebugHolder extends RecyclerView.ViewHolder {

    public BaseDebugHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nonnull DebugPresenter.BaseDebugItem card);

    public abstract void recycle();
}

public class DebugAdapter extends RecyclerView.Adapter<BaseDebugHolder> implements
        Action1<List<DebugPresenter.BaseDebugItem>> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_INFORMATION = 1;
    private static final int TYPE_SWITCH = 2;
    private static final int TYPE_OPTION = 3;
    private static final int TYPE_ACTION = 4;

    static class CategoryHolder extends BaseDebugHolder {

        TextView title;

        public CategoryHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.debug_category_title);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            DebugPresenter.CategoryItem categoryItem = (DebugPresenter.CategoryItem) item;
            title.setText(categoryItem.getTitle());

        }

        @Override
        public void recycle() {

        }

        public static CategoryHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new CategoryHolder(inflater.inflate(R.layout.debug_category_item, parent, false));
        }

    }

    static class InformationHolder extends BaseDebugHolder {

        TextView name;
        TextView value;

        public InformationHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.debug_info_name);
            value = (TextView) itemView.findViewById(R.id.debug_info_value);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            DebugPresenter.InformationItem informationItem = (DebugPresenter.InformationItem) item;

            name.setText(informationItem.getName());
            value.setText(informationItem.getValue());
            value.setSelected(true);
        }

        @Override
        public void recycle() {
        }

        public static InformationHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new InformationHolder(inflater.inflate(R.layout.debug_info_item, parent, false));
        }

    }

    static class OptionHolder extends BaseDebugHolder {

        private final View view;
        private Subscription mSubscription;

        TextView optionName;
        Button button;


        public OptionHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            optionName = (TextView) itemView.findViewById(R.id.debug_option_name);
            button = (Button) itemView.findViewById(R.id.debug_option_value);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            recycle();
            final DebugPresenter.OptionItem optionItem = (DebugPresenter.OptionItem) item;

            button.setText(String.format("%d", ResponseInterceptor.getResponseCode()));
            optionName.setText(optionItem.getName());
            mSubscription = new CompositeSubscription(
                    RxView.clicks(button).subscribe(optionItem.clickObserver()));

        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static OptionHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new OptionHolder(inflater.inflate(R.layout.debug_option_item, parent, false));
        }

    }

    static class SwitchHolder extends BaseDebugHolder {

        private final View view;
        private DebugHelperPreferences debugPreferences;
        private Subscription mSubscription;

        Switch debugSwitch;
        TextView title;

        public SwitchHolder(View itemView, DebugHelperPreferences debugPreferences) {
            super(itemView);
            this.view = itemView;
            this.debugPreferences = debugPreferences;
            debugSwitch = (Switch) itemView.findViewById(R.id.debug_switch);
            title = (TextView) itemView.findViewById(R.id.debug_switch_title);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            recycle();
            final DebugPresenter.SwitchItem switchItem = (DebugPresenter.SwitchItem) item;

            title.setText(switchItem.getTitle());

            mSubscription = new CompositeSubscription(
                    Observable.just(switchItem.isSwitcher())
                            .filter(new Func1<Boolean, Boolean>() {
                                @Override
                                public Boolean call(Boolean aBoolean) {
                                    return switchItem.getOption()== DebugOption.SET_EMPTY_RESPONSE;
                                }
                            })
                            .subscribe(RxCompoundButton.checked(debugSwitch)),
                    RxCompoundButton.checkedChanges(debugSwitch)
                            .skip(1)
                            .subscribe(switchItem.switchOption())
            );


        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static SwitchHolder create(ViewGroup parent, DebugHelperPreferences debugPreferences) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new SwitchHolder(inflater.inflate(R.layout.debug_switch_item, parent, false), debugPreferences);
        }

    }

    static class ActionHolder extends BaseDebugHolder {

        private final View view;
        private Subscription mSubscription;

        TextView actionName;

        public ActionHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            actionName = (TextView) itemView.findViewById(R.id.debug_info_name);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            recycle();
            DebugPresenter.ActionItem actionItem = (DebugPresenter.ActionItem) item;

            actionName.setText(actionItem.getName());

            mSubscription = new CompositeSubscription(
                    RxView.clicks(view)
                            .subscribe(actionItem.actionOption())
            );


        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static ActionHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ActionHolder(inflater.inflate(R.layout.debug_info_item, parent, false));
        }

    }

    @Nonnull
    private List<DebugPresenter.BaseDebugItem> baseDebugItems = ImmutableList.of();

    private DebugHelperPreferences debugPreferences;


    public DebugAdapter(DebugHelperPreferences debugPreferences) {
        this.debugPreferences = debugPreferences;
    }


    @Override
    public void call(List<DebugPresenter.BaseDebugItem> baseDebugItems) {
        this.baseDebugItems = baseDebugItems;
    }

    @Override
    public BaseDebugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY) {
            return CategoryHolder.create(parent);
        } else if (viewType == TYPE_INFORMATION) {
            return InformationHolder.create(parent);
        } else if (viewType == TYPE_SWITCH) {
            return SwitchHolder.create(parent, debugPreferences);
        } else if (viewType == TYPE_ACTION) {
            return ActionHolder.create(parent);
        } else if (viewType == TYPE_OPTION) {
            return OptionHolder.create(parent);
        }
        throw new RuntimeException("there is no type that matches the type "
                + viewType
                + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(BaseDebugHolder holder, int position) {
        holder.bind(baseDebugItems.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        final DebugPresenter.BaseDebugItem item = baseDebugItems.get(position);
        if (item instanceof DebugPresenter.CategoryItem) {
            return TYPE_CATEGORY;
        } else if (item instanceof DebugPresenter.InformationItem) {
            return TYPE_INFORMATION;
        } else if (item instanceof DebugPresenter.SwitchItem) {
            return TYPE_SWITCH;
        } else if (item instanceof DebugPresenter.OptionItem) {
            return TYPE_OPTION;
        } else if (item instanceof DebugPresenter.ActionItem) {
            return TYPE_ACTION;
        } else {
            throw new IllegalStateException("Cannot find item for position" + position);
        }
    }

    @Override
    public int getItemCount() {
        return baseDebugItems.size();
    }

    @Override
    public void onViewRecycled(BaseDebugHolder holder) {
        holder.recycle();
        super.onViewRecycled(holder);
    }
}
