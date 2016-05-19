package com.appunite.debughelper.macro;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appunite.debughelper.R;

import java.util.List;

public class MacroAdapter extends RecyclerView.Adapter<MacroAdapter.ViewHolder> {
    private final List<SavedMacro> mData;

    public MacroAdapter(List<SavedMacro> macroList) {
        mData = macroList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.macro_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView request;
        public TextView count;

        public ViewHolder(View itemView) {
            super(itemView);
            request = (TextView) itemView.findViewById(R.id.count_name);
            count = (TextView) itemView.findViewById(R.id.count_value);
        }
    }

}