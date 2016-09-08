package com.appunite.debughelper.macro;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.appunite.debughelper.DebugHelperPreferences;
import com.appunite.debughelper.R;
import com.appunite.debughelper.base.DebugActivity;
import com.appunite.debughelper.dialog.EditDialog;
import com.appunite.debughelper.utils.MacroSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class MacroFragment extends DialogFragment
        implements MacroAdapter.MacroListener, EditDialog.OnChangeNameListener {

    public interface MacroFragmentListener {
        void onFinishDialog();
    }

    private Context mContext;
    private DebugHelperPreferences debugHelperPreferences;
    private List<MacroItem<GenericSavedField>> macroItems;
    private ViewGroup viewGroup;
    private MacroAdapter adapter = new MacroAdapter(this, macroItems);
    private MacroFragmentListener listener;

    RecyclerView recyclerView;
    Button createMacroButton;
    TextView activityName;

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

        final View v = new TextView(this.getActivity());
        v.getClass();
        listener = (MacroFragmentListener) getActivity();

        final View rootView = inflater.inflate(R.layout.macro_layout, container, true);
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
                macroItems.add(new MacroItem(mergeFields(viewGroup.getChildAt(0)), mContext.hashCode()));
                adapter.update(macroItems);
                saveMacros(macroItems);
            }
        });

        return rootView;
    }

    public List<SavedField> mergeFields(View view) {
        final List<SavedField> completeFieldList = new ArrayList<>();

        if (view instanceof RecyclerView) {
//            final RecyclerView recycleItem = (RecyclerView) view;
//            if (recycleItem.getAdapter() instanceof MacroRecyclerViewListener) {
//                final MacroRecyclerViewListener recyclerViewDataHolder = (MacroRecyclerViewListener) recycleItem.getAdapter();
//
//                final MacroData macroData = recyclerViewDataHolder.macroData();
//                //TODO more items is lagging
//                if (macroData.getAdapterList().size() <= 15) {
//                    final String json = adapterGson().toJson(macroData.getAdapterList());
//                    completeFieldList.add(new SavedField(recycleItem.getId(), json, macroData.getAdapterType()));
//                }
//            } else {
//                Toast toast = Toast.makeText(getActivity(), "If you want save "
//                        + recycleItem.getAdapter().getClass().getSimpleName()
//                        + " data, implement "
//                        + MacroRecyclerViewListener.class.getSimpleName(), Toast.LENGTH_LONG);
//                toast.show();
//            }
        } else if (view instanceof Spinner) {
            final Spinner spinner = (Spinner) view;
            completeFieldList.add(new SavedField(spinner.getId(), spinner.getSelectedItemPosition()));
        } else if (view instanceof SearchView) {
            final SearchView searchView = (SearchView) view;
            completeFieldList.add(new SavedField(searchView.getId(), searchView.getQuery().toString()));
        } else if (view instanceof ViewGroup) {
            final ViewGroup parentView = (ViewGroup) view;
            final int childCount = parentView.getChildCount();

            for (int i = 0; i < childCount; i++) {
                completeFieldList.addAll(mergeFields(parentView.getChildAt(i)));
            }
        } else if (view instanceof EditText) {
            final EditText editText = (EditText) view;
            completeFieldList.add(new SavedField(editText.getId(), editText.getText().toString()));
        } else if (view instanceof CompoundButton) {
            final CompoundButton button = (CompoundButton) view;
            completeFieldList.add(new SavedField(button.getId(), button.isChecked()));
        }
        return completeFieldList;
    }

    public void doMacro(int position) {
        final MacroItem<GenericSavedField> macroItem = getMacroItems().get(position);
        final List<GenericSavedField> baseFieldItems = macroItem.getBaseFieldItems();

        for (GenericSavedField baseField : baseFieldItems) {
            if (baseField instanceof SavedField) {
                final SavedField savedField = (SavedField) baseField;
                View view = viewGroup.findViewById(savedField.getIdView());
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    editText.setText(savedField.getText());
                } else if (view instanceof CompoundButton) {
                    CompoundButton compoundButton = (CompoundButton) view;
                    compoundButton.setChecked(savedField.isChecked());
                } else if (view instanceof Spinner) {
                    Spinner spinner = (Spinner) view;
                    spinner.setSelection(savedField.getSelectedPosition());
                } else if (view instanceof SearchView) {
                    SearchView searchView = (SearchView) view;
                    searchView.setQuery(savedField.getText(), false);
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

    public void saveMacros(final List<MacroItem<GenericSavedField>> macroItems) {
        final Gson macroGson = createGson();
        final Type collectionType = new TypeToken<List<MacroItem<GenericSavedField>>>() {}.getType();
        final String serializedMacros = macroGson.toJson(macroItems, collectionType);
        debugHelperPreferences.saveMacroList(serializedMacros);

    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(GenericSavedField.class, new MacroSerializer())
                .create();
    }

    public List<MacroItem<GenericSavedField>> getMacroItems() {
        final Gson gson = createGson();

        final String macroList = debugHelperPreferences.getMacroList();
        Type collectionType = new TypeToken<List<MacroItem<GenericSavedField>>>() {

        }.getType();
        return gson.fromJson(macroList, collectionType);
    }

    @Override
    public void useMacro(final int position) {
        doMacro(position);
        listener.onFinishDialog();
        dismiss();
    }

    @Override
    public void editMacro(final int position, final String name) {
        final EditDialog editDialog = EditDialog.newInstance(position, name);
        editDialog.setTargetFragment(this, 0);
        editDialog.show(this.getFragmentManager(), "EditMacro");
    }

    @Override
    public void deleteMacro(final int position) {
        macroItems.remove(position);
        saveMacros(macroItems);
        adapter.update(macroItems);
    }

    @Override
    public void onChangeName(final int position,@Nonnull final String newName) {
        macroItems.get(position).setMacroName(newName);
        saveMacros(macroItems);
        adapter.update(macroItems);
    }

}

//TODO GET MACRO ITEMS FILTER

//    public List<Object> getMacroItems() {
//        String[] macroList = debugHelperPreferences.getMacroList().split(macroDivider);
//
//        for (String baseField : macroList) {
//            String[] fieldList = baseField.split(savedFieldName);
//            boolean switchA = false;
//            for (String field : fieldList) {
//                switchA = !switchA;
//                if (switchA) {
//                    Type collectionType = new TypeToken<List<SavedField>>() {
//
//                    }.getType();
//
//                    final Gson gson = new GsonBuilder().create();
//                    return gson.fromJson(field, collectionType);
//                } else {
//                    String[] unknownField = field.split(unknownName);
//                    for (String string : unknownField) {
//
//                    }
//                }
//
//            }
//
//        }
//    }

//    public void saveMacros(List<MacroItem> macroItems) {
//        StringBuilder allData = new StringBuilder();
//        final Gson gson = new Gson();
//        for (MacroItem macroItem : macroItems) {
//            allData.append(macroDivider);
//            StringBuilder fields = new StringBuilder();
//            for (int i = 0; i < macroItem.getBaseFieldItems().size(); i++) {
//                Object field = macroItem.getBaseFieldItems().get(i);
//
//                if (field instanceof BaseFieldItem) {
//                    fields.append(unknownName);
//                    StringBuilder baseField = new StringBuilder();
//                    BaseFieldItem baseFieldItem = (BaseFieldItem) field;
//                    String jsonField = gson.toJson(baseFieldItem.getUserAdapterItems());
//                    baseField.append(jsonField);
//                    baseField.append(smallDivider);
//                    baseField.append(gson.toJson(baseFieldItem.getClasses()));
//                    fields.append(baseField);
//                } else {
//                    fields.append(savedFieldName);
//                    fields.append(gson.toJson(field));
//                }
//
//            }
//            allData.append(fields);
//        }
//        debugHelperPreferences.saveMacroList(allData.toString());
//    }

//    public List<MacroItem> getMacroItems() {
//        String json = debugHelperPreferences.getMacroList();
//        Type collectionType = new TypeToken<List<MacroItem>>() {
//
//        }.getType();
//
//        final Gson gson = new GsonBuilder().create();
//        return gson.fromJson(json, collectionType);
//    }

//
//        if (changedViews.isEmpty()) {
//            List<SavedField> filteredMacros = savedFields;
//            for (int k = 0; k < changedViews.size(); k++) {
//                for (int j = 0; j < filteredMacros.size(); j++) {
//                    if (changedViews.get(k).getIdView().equals(filteredMacros.get(j).getIdView())) {
//                        filteredMacros.remove(j);
//                        break;
//                    }
//                }
//            }
//        }