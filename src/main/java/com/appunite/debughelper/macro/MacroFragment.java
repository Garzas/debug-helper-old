package com.appunite.debughelper.macro;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.appunite.debughelper.DebugHelperPreferences;
import com.appunite.debughelper.R;
import com.appunite.debughelper.dialog.EditDialog;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MacroFragment extends DialogFragment
        implements MacroAdapter.UseMacroListener, EditDialog.OnChangeNameListener {

    private Context mContext;
    private DebugHelperPreferences debugHelperPreferences;
    private List<MacroItem> macroItems;
    private List<SavedMacro> savedMacros;
    private ViewGroup viewGroup;
    private MacroAdapter adapter = new MacroAdapter(this, macroItems);

    RecyclerView recyclerView;
    Button createMacroButton;
    TextView activityName;

    public static Fragment newInstance() {
        return new MacroFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.macro_layout, container, true);
        mContext = getActivity();
        viewGroup = (ViewGroup) getActivity().findViewById(R.id.main_frame);
        debugHelperPreferences = new DebugHelperPreferences(mContext);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.macro_recyclerview);
        recyclerView.setLayoutManager(mLayoutManager);
        createMacroButton = (Button) rootView.findViewById(R.id.create_macro_button);
        activityName = (TextView) rootView.findViewById(R.id.macro_activity_name);

        macroItems = getMacroItems();

        adapter = new MacroAdapter(this, macroItems);
        recyclerView.setAdapter(adapter);
        activityName.setText(getActivity().getClass().getSimpleName());

        createMacroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedMacros = createMacro(viewGroup.getChildAt(0));

                macroItems.add(new MacroItem(savedMacros, mContext.hashCode()));
                adapter.update(macroItems);
                saveMacros(macroItems);
            }
        });

        return rootView;
    }

    public List<SavedMacro> createMacro(View view) {
        final List<SavedMacro> macroModelList = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) view;
            final int childCount = parentView.getChildCount();

            for (int i = 0; i < childCount; i++) {
                macroModelList.addAll(createMacro(parentView.getChildAt(i)));
            }

        } else if (view instanceof EditText) {
            EditText editText = (EditText) view;
            macroModelList.add(new SavedMacro(editText.getId(), editText.getText().toString()));
        } else if (view instanceof CompoundButton) {
            CompoundButton button = (CompoundButton) view;
            macroModelList.add(new SavedMacro(button.getId(), button.isChecked()));
        }

        return macroModelList;
    }

    public void doMacro(int position) {
        List<SavedMacro> macros = macroItems.get(position).getMacroList();
        for (int i = 0; i < macros.size(); i++) {
            final SavedMacro savedMacro = macros.get(i);
            View view = viewGroup.findViewById(savedMacro.getIdView());
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.setText(savedMacro.getText());
            }
            //TODO MACRO OTHER VIEWS
        }
    }

    public void saveMacros(List<MacroItem> macroItems) {
        String json = new Gson().toJson(macroItems);
        debugHelperPreferences.saveMacroList(json);
    }

    public List<MacroItem> getMacroItems() {
        String json = debugHelperPreferences.getMacroList();
        Type collectionType = new TypeToken<List<MacroItem>>() {

        }.getType();

        final Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, collectionType);
    }

    @Override
    public void useMacro(int position) {
        doMacro(position);
    }

    @Override
    public void editMacro(int position, String name) {
        EditDialog editDialog = EditDialog.newInstance(position, name);
        editDialog.setTargetFragment(this, 0);
        editDialog.show(this.getFragmentManager(), "EditMacro");
    }

    @Override
    public void onChangeName(int position, String newName) {
        macroItems.get(position).setMacroName(newName);
        saveMacros(macroItems);
        adapter.update(macroItems);
    }
}