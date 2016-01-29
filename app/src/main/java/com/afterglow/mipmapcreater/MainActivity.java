package com.afterglow.mipmapcreater;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.github.syplanp.util.graphics.ImageConvertUtil;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static ImageView terrain_view;
    private static ScrollView scroll;
    private static ImageView mip0_view;
    private static ImageView mip1_view;
    private static ImageView mip2_view;
    private static ImageView mip3_view;

    private static Bitmap terrain;
    private static FloatingActionButton fab;
    public static String terrain_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        terrain_view = (ImageView) findViewById(R.id.main_tga);
        scroll = (ScrollView) findViewById(R.id.mipmap_scroll);
        mip0_view = (ImageView) findViewById(R.id.mip0_tga);
        mip1_view = (ImageView) findViewById(R.id.mip1_tga);
        mip2_view = (ImageView) findViewById(R.id.mip2_tga);
        mip3_view = (ImageView) findViewById(R.id.mip3_tga);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {
            Snackbar.make(fab, "제작: SY PlanP - Copytight 2016. SY all rights reserved", Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void onDrawTerrain() {
        Log.d("terrain path", terrain_path);

        try {
            terrain = ImageConvertUtil.TGALoader(terrain_path);

            if(terrain == null) {
                Snackbar.make(fab, "파일분석을 실패하였습니다", Snackbar.LENGTH_SHORT).show();
                return;
            }

            terrain_view.setImageBitmap(terrain);

            Bitmap terrain_mip1 = Bitmap.createScaledBitmap(terrain, terrain.getWidth() / 2, terrain.getHeight() / 2, true);
            Bitmap terrain_mip2 = Bitmap.createScaledBitmap(terrain, terrain.getWidth() / 4, terrain.getHeight() / 4, true);
            Bitmap terrain_mip3 = Bitmap.createScaledBitmap(terrain, terrain.getWidth() / 8, terrain.getHeight() / 8, true);
            Bitmap terrain_mip4 = Bitmap.createScaledBitmap(terrain, terrain.getWidth() / 16, terrain.getHeight() / 16, true);

            mip0_view.setImageBitmap(terrain_mip1);
            mip1_view.setImageBitmap(terrain_mip2);
            mip2_view.setImageBitmap(terrain_mip3);
            mip3_view.setImageBitmap(terrain_mip4);

            mip0_view.setMinimumWidth(terrain.getWidth());
            mip0_view.setMinimumHeight(terrain.getHeight());
            mip1_view.setMinimumWidth(terrain.getWidth());
            mip1_view.setMinimumHeight(terrain.getHeight());
            mip2_view.setMinimumWidth(terrain.getWidth());
            mip2_view.setMinimumHeight(terrain.getHeight());
            mip3_view.setMinimumWidth(terrain.getWidth());
            mip3_view.setMinimumHeight(terrain.getHeight());

            scroll.setVisibility(View.VISIBLE);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void genarateMipmap(View v) {
        if(terrain_path.equals("") || terrain_path == null) {
            Snackbar.make(fab, "밉맵을 생성할 Terrain파일을 선택해주세요.", Snackbar.LENGTH_SHORT).show();
        } else {
            Bitmap terrain_mip1 = Bitmap.createScaledBitmap(terrain, terrain.getWidth() / 2, terrain.getHeight() / 2, true);
            Bitmap terrain_mip2 = Bitmap.createScaledBitmap(terrain, terrain.getWidth() / 4, terrain.getHeight() / 4, true);
            Bitmap terrain_mip3 = Bitmap.createScaledBitmap(terrain, terrain.getWidth() / 8, terrain.getHeight() / 8, true);
            Bitmap terrain_mip4 = Bitmap.createScaledBitmap(terrain, terrain.getWidth() / 16, terrain.getHeight() / 16, true);

            try {
                ImageConvertUtil.TGAWriter(terrain_mip1, new File(Environment.getExternalStorageDirectory() + "/mipmap/terrain-atlas_mip0.tga"));
                ImageConvertUtil.TGAWriter(terrain_mip2, new File(Environment.getExternalStorageDirectory() + "/mipmap/terrain-atlas_mip1.tga"));
                ImageConvertUtil.TGAWriter(terrain_mip3, new File(Environment.getExternalStorageDirectory() + "/mipmap/terrain-atlas_mip2.tga"));
                ImageConvertUtil.TGAWriter(terrain_mip4, new File(Environment.getExternalStorageDirectory() + "/mipmap/terrain-atlas_mip3.tga"));
            } catch(Exception e) {
                e.printStackTrace();

                Snackbar.make(fab, "밉맵을 생성하는도중 오류가 났습니다", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Snackbar.make(fab, "mipmap 폴더에 밉맵들이 저장되었습니다", Snackbar.LENGTH_SHORT).show();
        }
    }
}
