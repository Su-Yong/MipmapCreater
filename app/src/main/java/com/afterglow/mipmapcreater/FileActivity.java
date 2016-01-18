package com.afterglow.mipmapcreater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileActivity extends AppCompatActivity {
    private File[] file_list;
    private Object[] listData;
    private static RecyclerView list;
    private static Context ctx;
    private static AppCompatActivity app;
    public static String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ctx = this;
        app = this;

        Log.d("path", Environment.getExternalStorageDirectory() + "");
        listData = getList(Environment.getExternalStorageDirectory() + "");

        FileListAdapter adapter = new FileListAdapter(listData);

        list = (RecyclerView) findViewById(R.id.file_list);

        list.setHasFixedSize(true);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    private static Object[] getList(String path) {
        ArrayList<String> folder_result = new ArrayList<>();
        ArrayList<String> file_result = new ArrayList<>();
        ArrayList<Bitmap> icon_result = new ArrayList<>();

        FileActivity.path = path;
        File[] f = new File(path).listFiles();
        Bitmap folder = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_folder_white_48dp);
        Bitmap file= BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_insert_drive_file_white_48dp);

        for(int i = 0; i < f.length; i++) {
            if(f[i].isDirectory()) {
                folder_result.add(f[i].getName());
            } else {
                file_result.add(f[i].getName());
            }
        }

        Collections.sort(folder_result, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(file_result, String.CASE_INSENSITIVE_ORDER);

        for(int i = 0; i < folder_result.size(); i++) {
            icon_result.add(folder);
        }
        for(int i = 0; i < file_result.size(); i++) {
            icon_result.add(file);
        }

        Object[] result_obj = new Object[3];
        result_obj[0] = folder_result;
        result_obj[1] = file_result;
        result_obj[2] = icon_result;

        return result_obj;
    }

    @Override
    public void onBackPressed() {
        File file = new File(FileActivity.path);

        if(file.toString().equals(Environment.getExternalStorageDirectory() + "")) {
            finish();
        } else {
            updatePath(file.getParent());
        }
    }

    public static void updatePath(String new_path) {
        String path = FileActivity.path = new_path;

        FileListAdapter adapter = new FileListAdapter(getList(path));

        list.setAdapter(adapter);
    }

    public static void finThis() {
        app.finish();
    }
}
