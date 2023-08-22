package com.idjmao.lib.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.idjmao.lib.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class FileLineAdapter extends RecyclerView.Adapter< FileLineAdapter.FileLineViewHolder> {
    List<File> fileList;

    public FileLineAdapter(List<File> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_file_line_list, parent, false);
        return new FileLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileLineViewHolder holder, int position) {
        File file=fileList.get(position);
        if (!file.isFile()){
            holder.icIv.setImageResource(R.drawable.ic_folder_24);
        }else if (file.getName().contains(".jpg")||file.getName().contains(".jpeg")||file.getName().contains(".png")){
            Glide.with(holder.icIv).load(file).into(holder.icIv);
        }else if (file.getName().contains(".txt")){
            holder.icIv.setImageResource(R.drawable.ic_text_file_24);
        }else if (file.getName().contains(".mp3")||file.getName().contains(".amr")||file.getName().contains(".aac")){
            holder.icIv.setImageResource(R.drawable.ic_audio_file_24);
        }else if (file.getName().contains(".mp4")){
            holder.icIv.setImageResource(R.drawable.ic_video_file_24);
        }else {
            holder.icIv.setImageResource(R.drawable.ic_help_center_24);
        }
        holder.nameTv.setText(file.getName());

        DecimalFormat df = new DecimalFormat("0.0");
        if (file.isFile()){
            float fileSize=file.length();
            Log.i("TAG", "onBindViewHolder: "+fileSize);
            holder.desTv.setText(file.length()+"");
            if (fileSize>(1024*1024*1024)){
                holder.desTv.setText(df.format(fileSize/(1024*1024*1024))+"GB");
            }else if (fileSize>(1024*1024)){
                holder.desTv.setText(df.format(fileSize/(1024*1024))+"MB");
            }else if (fileSize>(1024)){
                holder.desTv.setText(df.format(fileSize/(1024))+"KB");
            }else {
                holder.desTv.setText(fileSize+"B");
            }

        }else {
            holder.desTv.setText(file.listFiles().length+"项");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onFileClick(file);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onFileClick(File file);
    }

    class FileLineViewHolder extends RecyclerView.ViewHolder {
        ImageView icIv;
        TextView nameTv,desTv;
        public FileLineViewHolder(@NonNull View itemView) {
            super(itemView);
            icIv=itemView.findViewById(R.id.file_ic_iv);
            nameTv=itemView.findViewById(R.id.file_name_tv);
            desTv=itemView.findViewById(R.id.file_des_tv);
        }
    }
}
