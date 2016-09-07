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
import android.widget.Toast;

import com.appunite.debughelper.DebugHelperPreferences;
import com.appunite.debughelper.R;
import com.appunite.debughelper.dialog.EditDialog;
import com.appunite.debughelper.listener.MacroRecyclerViewListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MacroFragment extends DialogFragment
        implements MacroAdapter.MacroListener, EditDialog.OnChangeNameListener {

    private Context mContext;
    private DebugHelperPreferences debugHelperPreferences;
    private List<MacroItem<GenericSavedField>> macroItems;
    private ViewGroup viewGroup;
    private MacroAdapter adapter = new MacroAdapter(this, macroItems);
    MacroRecyclerViewListener recyclerViewDataHolder;

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

        View v = new TextView(this.getActivity());
        v.getClass();

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
                macroItems.add(new MacroItem(mergeFields(viewGroup.getChildAt(0)), mContext.hashCode()));
                adapter.update(macroItems);
                saveMacros(macroItems);
            }
        });

        return rootView;
    }

    public List<Object> mergeFields(View view) {
        final List<Object> completeFieldList = new ArrayList<>();

        if (view instanceof RecyclerView) {
            final RecyclerView recycleItem = (RecyclerView) view;
            if (recycleItem.getAdapter() instanceof MacroRecyclerViewListener) {
                recyclerViewDataHolder = (MacroRecyclerViewListener) recycleItem.getAdapter();
                final List<GenericSavedField> recyclerViewItems = recyclerViewDataHolder.macroData();
                if (recyclerViewItems.size() <= 15) {
                    completeFieldList.add(new RecycleViewItem<>(recyclerViewItems));
                }
            } else {
                Toast toast = Toast.makeText(getActivity(), "If you want save "
                        + recyclerView.getAdapter().getClass().getSimpleName()
                        + " data, implement "
                        + MacroRecyclerViewListener.class.getSimpleName(), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (view instanceof Spinner) {
            completeFieldList.add(createSpinnerMacro(view));
        } else if (view instanceof SearchView) {
            SearchView searchView = (SearchView) view;
            completeFieldList.add(new SavedField(searchView.getId(), searchView.getQuery().toString()));
        } else if (view instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) view;
            final int childCount = parentView.getChildCount();

            for (int i = 0; i < childCount; i++) {
                completeFieldList.addAll(mergeFields(parentView.getChildAt(i)));
            }
        } else if (view instanceof EditText) {
            EditText editText = (EditText) view;
            completeFieldList.add(new SavedField(editText.getId(), editText.getText().toString()));
        } else if (view instanceof CompoundButton) {
            CompoundButton button = (CompoundButton) view;
            completeFieldList.add(new SavedField(button.getId(), button.isChecked()));
        }
        return completeFieldList;
    }

    public List<SavedField> createSpinnerMacro(View view) {
        List<SavedField> newList = new ArrayList<>();
        Spinner spinner = (Spinner) view;
        newList.add(new SavedField(spinner.getId(), spinner.getSelectedItemPosition()));
        return newList;
    }

    public void doMacro(int position) {
        MacroItem<GenericSavedField> macroItem = getMacroItems().get(position);
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
                } else {
                    //TODO list all not updated items
                }
            } else if (baseField instanceof RecycleViewItem) {
                RecycleViewItem recycleViewItem = (RecycleViewItem) baseField;
                recyclerViewDataHolder.fillFields(recycleViewItem.getRecyclerViewItems());
            }
        }

    }

    public void saveMacros(List<MacroItem<GenericSavedField>> macroItems) {
        Gson macroGson = createGson();
        final Type collectionType = new TypeToken<List<MacroItem<GenericSavedField>>>() {}.getType();
        String serializedMacros = macroGson.toJson(macroItems, collectionType);
        debugHelperPreferences.saveMacroList(serializedMacros);

    }

    private static class DebugSerializer implements JsonDeserializer<GenericSavedField>, JsonSerializer<GenericSavedField> {

        @Override
        public GenericSavedField deserialize(final JsonElement json,
                                             final Type typeOfT,
                                             final JsonDeserializationContext context) throws JsonParseException {
            try {
                final String type = json.getAsJsonObject().get("type").getAsString();
                final Class<?> clazz = Class.forName(type);
                return context.deserialize(json, clazz);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(final GenericSavedField src,
                                     final Type typeOfSrc,
                                     final JsonSerializationContext context) {
            final JsonObject serialized = (JsonObject) context.serialize(src);
            serialized.addProperty("type", src.getClass().getName());
            return serialized;
        }
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(GenericSavedField.class, new DebugSerializer())
                .create();
    }

    public List<MacroItem<GenericSavedField>> getMacroItems() {
        Gson gson = createGson();

        final String macroList = debugHelperPreferences.getMacroList();
        Type collectionType = new TypeToken<List<MacroItem<GenericSavedField>>>() {

        }.getType();
        return gson.fromJson(macroList, collectionType);
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
    public void deleteMacro(int position) {
        macroItems.remove(position);
        saveMacros(macroItems);
        adapter.update(macroItems);
    }

    @Override
    public void onChangeName(int position, String newName) {
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

//TODO show views not found or implement overriding method
//        }