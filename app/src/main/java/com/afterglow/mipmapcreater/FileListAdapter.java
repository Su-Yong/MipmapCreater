package com.afterglow.mipmapcreater;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class FileListAdapter extends RecyclerView.Adapter {
    private ArrayList<String> folder_list;
    private ArrayList<String> file_list;
    private ArrayList<Bitmap> icon_list;

    public FileListAdapter(Object[] list) {
        this.folder_list = (ArrayList<String>) list[0];
        this.file_list = (ArrayList<String>) list[1];
        this.icon_list = (ArrayList<Bitmap>) list[2];
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_content, viewGroup, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String name = "";
        Boolean isDirectory = false;

        try {
            name = folder_list.get(position);
            isDirectory = true;
        } catch(Exception e) {
            name = file_list.get(position - folder_list.size());
        }

        final String name_ = name;
        final Boolean isDirectory_ = isDirectory;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDirectory_) {
                    FileActivity.updatePath(FileActivity.path + "/" + name_);
                } else {
                    MainActivity.terrain_path = FileActivity.path + "/" + name_;
                    FileActivity.finThis();
                    MainActivity.onDrawTerrain();
                }
            }
        });

        ((ImageView) holder.itemView.findViewById(R.id.file_icon)).setImageBitmap(icon_list.get(position));
        ((TextView) holder.itemView.findViewById(R.id.file_name)).setText(name);
    }

    @Override
    public int getItemCount() {
        return icon_list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView text;
        public ImageView icon;

        public Holder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.file_name);
            icon = (ImageView) itemView.findViewById(R.id.file_icon);
        }
    }
}