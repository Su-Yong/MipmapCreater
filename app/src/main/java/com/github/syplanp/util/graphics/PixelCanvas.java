package com.github.syplanp.util.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class PixelCanvas extends ImageView implements View.OnTouchListener {
    private int color = 0xffffffff;
    private int width_count = 0;
    private int height_count = 0;
    private boolean isInit = false;
    private boolean isUpdate = false;
    private int width = 0;
    private int height = 0;

    public PixelCanvas(Context context,int width, int height) {
        super(context);

        this.width = width;
        this.height = height;

        onCreate();
    }

    public PixelCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public void setSplitWidth(int width) {
        width_count = width;
    }
    public void setSplitHeight(int height) {
        height_count = height;
    }

    public void setColor(int color) {
        this.color = color;
    }
    public int getColor() {
        return color;
    }

    public void init() throws Exception {
        if(width_count <= 0 || height_count <= 0) {
            throw new Exception("Splited width or height count must be > 0");
        } else {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();

            paint.setARGB(255, 128, 128, 128);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);

            for (int x = 0; x < width_count; x++) {
                for (int y = 0; y < height_count; y++) {
                    int xx = (x * (width / width_count));
                    int yy = (y * (height / height_count));
                    int xx2 = ((x + 1) * (width / width_count));
                    int yy2 = ((y + 1) * (height / height_count));

                    canvas.drawRect(xx, yy, xx2, yy2, paint);
                }
            }

            setImageBitmap(bitmap);

            isInit = true;
        }
    }

    private void onCreate() {
        setClickable(true);
        setOnTouchListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(isUpdate && hasFocus) {

            int width = this.getWidth();
            int height = this.getHeight();

            Log.d("Width, Height", width + " " + height);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();

            paint.setARGB(255, 128, 128, 128);

            for (int x = 0; x < width_count; x++) {
                canvas.drawLine((width / width_count) * x, 0, (width / width_count) * x, height, paint);
            }
            for (int y = 0; y < width_count; y++) {
                canvas.drawLine(0, (height / height_count) * y, width, (height / height_count) * y, paint);
            }

            Log.d("Width, Height count", width_count + " " + height_count);

            setImageBitmap(bitmap);

            isInit = true;
            isUpdate = false;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        return false;
    }
}
