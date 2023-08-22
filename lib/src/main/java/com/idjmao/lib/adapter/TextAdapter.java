package com.idjmao.lib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idjmao.lib.R;

import java.util.List;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextVh> {

    List<String> keyList;

    public TextAdapter(List<String> keyList) {
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public TextVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text,parent,false);
        return new TextVh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextVh holder, int position) {
        holder.keyTv.setText(keyList.get(position));
        holder.keyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener!=null){
                    onClickListener.onClick(holder.getAdapterPosition(), keyList.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    public class TextVh extends RecyclerView.ViewHolder{
        TextView keyTv;
        public TextVh(@NonNull View itemView) {
            super(itemView);
            keyTv=itemView.findViewById(R.id.item_text);
        }
    }

    AdapterOnClickListener<String> onClickListener;

    public void setOnClickListener(AdapterOnClickListener<String> onClickListener) {
        this.onClickListener = onClickListener;
    }
}
