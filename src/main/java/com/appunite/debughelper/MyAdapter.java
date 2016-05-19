package com.appunite.debughelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private final ArrayList mData;
    TextView key;
    TextView value;

    public MyAdapter(Map<String, Integer> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.debug_info_item, parent, false);
        } else {
            view = convertView;
        }
        key = (TextView) view.findViewById(R.id.debug_info_name);
        value = (TextView) view.findViewById(R.id.debug_info_value);


        Map.Entry<String, String> item = getItem(position);

        key.setText(item.getKey());
        value.setText(String.format("Counts %s", item.getValue()));

        return view;
    }
}