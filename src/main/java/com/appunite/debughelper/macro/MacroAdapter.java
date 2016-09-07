package com.appunite.debughelper.macro;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appunite.debughelper.R;

import java.util.List;

public class MacroAdapter extends RecyclerView.Adapter<MacroAdapter.MacroHolder> {

    private List<MacroItem<GenericSavedField>> mData;
    private MacroListener listener;

    public MacroAdapter(MacroListener listener, List<MacroItem<GenericSavedField>> macroItems) {
        this.listener = listener;
        mData = macroItems;
    }

    @Override
    public MacroHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.macro_item, parent, false);
        return new MacroHolder(v);
    }

    @Override
    public void onBindViewHolder(final MacroHolder holder, int position) {
        holder.macroName.setText(mData.get(position).getMacroName());
        holder.useMacro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.useMacro(holder.getAdapterPosition());
                }
            }
        });

        holder.editMacro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editMacro(holder.getAdapterPosition(), holder.macroName.getText().toString());
            }
        });

        holder.deleteMacro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteMacro(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void update(List<MacroItem<GenericSavedField>> macroItems) {
        mData = macroItems;
        notifyDataSetChanged();
    }

    public static class MacroHolder extends RecyclerView.ViewHolder {
        TextView macroName;
        ImageButton useMacro;
        ImageButton editMacro;
        ImageButton deleteMacro;

        public MacroHolder(View itemView) {
            super(itemView);
            macroName = (TextView) itemView.findViewById(R.id.macro_name);
            useMacro = (ImageButton) itemView.findViewById(R.id.use_macro);
            editMacro = (ImageButton) itemView.findViewById(R.id.edit_macro);
            deleteMacro = (ImageButton) itemView.findViewById(R.id.remove_macro);
        }
    }

    public interface MacroListener {
        void useMacro(int position);
        void editMacro(int position, String name);
        void deleteMacro(int position);
    }

}