package com.appunite.debughelper;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;



public class InfoListFragment extends DialogFragment {
    protected ListView listView;
    protected RecyclerView.LayoutManager mLayoutManager;

    static MyAdapter adapter;

    public static Fragment newInstance() {
        return new InfoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, true);


        mLayoutManager = new LinearLayoutManager(getActivity());
        listView = (ListView) rootView.findViewById(R.id.info_listview);

        adapter = new MyAdapter(ResponseInterceptor.getRequestCounter());
        listView.setAdapter(adapter);

        return rootView;
    }

    public static void updateAdapter() {
        adapter.notifyDataSetChanged();
    }
}