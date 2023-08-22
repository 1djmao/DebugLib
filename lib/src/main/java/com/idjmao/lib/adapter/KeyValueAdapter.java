package com.idjmao.lib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idjmao.lib.R;

import java.util.List;

public class KeyValueAdapter extends RecyclerView.Adapter<KeyValueAdapter.KeyValueVh> {

    List<String> keyList;
    List<String> valueList;

    public KeyValueAdapter(List<String> keyList, List<String> valueList) {
        this.keyList = keyList;
        this.valueList = valueList;
    }

    @NonNull
    @Override
    public KeyValueVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_2_text,parent,false);
        return new KeyValueVh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyValueVh holder, int position) {
        holder.keyTv.setText(keyList.get(position));
        holder.valueTv.setText(valueList.get(position));
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    public class KeyValueVh extends RecyclerView.ViewHolder{
        TextView keyTv,valueTv;
        public KeyValueVh(@NonNull View itemView) {
            super(itemView);
            keyTv=itemView.findViewById(R.id.key_text);
            valueTv=itemView.findViewById(R.id.value_text);
        }
    }
}
