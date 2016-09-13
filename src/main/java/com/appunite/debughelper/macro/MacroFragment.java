package com.appunite.debughelper.macro;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.appunite.debughelper.R;
import com.appunite.debughelper.dialog.EditDialog;
import com.appunite.debughelper.model.EditMacro;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import javax.annotation.Nonnull;

import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class MacroFragment extends DialogFragment
        implements EditDialog.OnChangeNameListener, MacroAdapter.MacroListener {

    public interface MacroFragmentListener {

        void onFinishDialog();
    }

    private Context mContext;
    private ViewGroup viewGroup;
    private MacroFragmentListener listener;
    private MacroAdapter adapter = new MacroAdapter(this);

    RecyclerView recyclerView;
    Button createMacroButton;

    MacroPresenter presenter;

    private final SerialSubscription subscriptions = new SerialSubscription();

    public static Fragment newInstance() {
        return new MacroFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        listener = (MacroFragmentListener) getActivity();
        presenter = new MacroPresenter(getActivity());

        final View rootView = inflater.inflate(R.layout.macro_layout, container, true);
        mContext = getActivity();
        viewGroup = (ViewGroup) getActivity().findViewById(R.id.main_frame);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.macro_recyclerview);
        recyclerView.setLayoutManager(mLayoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);
        ((LinearLayoutManager) recyclerView.getLayoutManager()).setRecycleChildrenOnDetach(true);
        createMacroButton = (Button) rootView.findViewById(R.id.create_macro_button);
        recyclerView.setAdapter(adapter);


        subscriptions.set(new CompositeSubscription(
                presenter.getMacroListObservable()
                        .mergeWith(presenter.getSaveMacrosObservable())
                        .subscribe(adapter),
                RxView.clicks(createMacroButton)
                        .map(new Func1<Void, ViewGroup>() {
                            @Override
                            public ViewGroup call(final Void aVoid) {
                                return viewGroup;
                            }
                        })
                        .subscribe(presenter.createClickObserver()),
                presenter.subscribe

        ));


        return rootView;
    }

    @Override
    public void onDestroyView() {
        subscriptions.set(Subscriptions.empty());
        super.onDestroyView();
    }

    public void doMacro(final MacroPresenter.MacroItem macroItem) {
        final List<SavedField> baseFieldItems = macroItem.getBaseFieldItems();

        for (SavedField baseField : baseFieldItems) {
            if (baseField != null) {
                View view = viewGroup.findViewById(baseField.getIdView());
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    editText.setText(baseField.getText());
                } else if (view instanceof CompoundButton) {
                    CompoundButton compoundButton = (CompoundButton) view;
                    compoundButton.setChecked(baseField.isChecked());
                } else if (view instanceof Spinner) {
                    Spinner spinner = (Spinner) view;
                    spinner.setSelection(baseField.getSelectedPosition());
                } else if (view instanceof SearchView) {
                    SearchView searchView = (SearchView) view;
                    searchView.setQuery(baseField.getText(), false);
//                } else if (view instanceof RecyclerView) {
//                    RecyclerView recyclerView = (RecyclerView) view;
//                    MacroRecyclerViewListener listener = (MacroRecyclerViewListener) recyclerView.getAdapter();
//
//                    listener.fillFields((List) adapterGson().fromJson(savedField.getJson(), savedField.getTypeToken()));
                } else {
                    //TODO list all not updated items
                }
            }
        }
    }

    @Override
    public void onChangeName(final int position, @Nonnull final String newName) {
        presenter.changeNameObserver().onNext(new EditMacro(position, newName));
    }

    @Override
    public void useMacro(MacroPresenter.MacroItem macroItem) {
        doMacro(macroItem);
        listener.onFinishDialog();
        dismiss();
    }

    @Override
    public void editMacro(final EditMacro editMacro) {
        final EditDialog editDialog = EditDialog.newInstance(editMacro.getPosition(), editMacro.getName());
        editDialog.setTargetFragment(MacroFragment.this, 0);
        editDialog.show(MacroFragment.this.getFragmentManager(), mContext.getString(R.string.edit_macro));
    }

    @Override
    public void deleteMacro(final int position) {
        presenter.deleteMacroObserver().onNext(position);
    }

}