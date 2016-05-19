package com.appunite.debughelper.macro;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appunite.debughelper.R;


public class MacroFragment extends DialogFragment {
     RecyclerView recyclerView;
     RecyclerView.LayoutManager mLayoutManager;
    Button createMacroButton;

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

        ViewGroup viewGroup = (ViewGroup) getActivity().findViewById(R.id.main_frame);
        final int childCount = viewGroup.getChildCount();
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.macro_recyclerview);
        recyclerView.setLayoutManager(mLayoutManager);

        DebugAutoFill debugAutoFill = new DebugAutoFill(viewGroup.getChildAt(0), getActivity().hashCode());
        final MacroAdapter adapter = new MacroAdapter(debugAutoFill.getMacroList());
        recyclerView.setAdapter(adapter);

        createMacroButton = (Button) rootView.findViewById(R.id.create_macro_button);

        return rootView;
    }

}