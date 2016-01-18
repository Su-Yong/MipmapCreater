package com.github.syplanp.util.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class ImageConvertUtil {
    private static int offset = 0;

    public static Bitmap TGALoader(String path) throws IOException {
        File file = new File(path);
        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        bis.read(buffer);

        return decode(buffer);
    }

    private static int btoi(byte b) {
        int a = b;
        return (a < 0) ? 256 + a : a;
    }

    private static int read(byte[] buf) {
        return btoi(buf[offset++]);
    }

    public static Bitmap decode(byte[] buf) throws IOException {
        offset = 0;

        for (int i = 0; i < 12; i++) {
            read(buf);
        }
        int width = read(buf) + (read(buf) << 8);
        int height = read(buf) + (read(buf) << 8);
        read(buf);
        read(buf);

        int n = width * height;
        int[] pixels = new int[n];
        int idx = 0;

        if (buf[2] == 0x02 && buf[16] == 0x20) {
            while(n > 0) {
                int b = read(buf);
                int g = read(buf);
                int r = read(buf);
                int a = read(buf);
                int v = (a << 24) | (r << 16) | (g << 8) | b;
                pixels[idx++] = v;
                n -= 1;
            }
        } else if (buf[2] == 0x02 && buf[16] == 0x18) {
            while(n > 0) {
                int b = read(buf);
                int g = read(buf);
                int r = read(buf);
                int a = 255;
                int v = (a << 24) | (r << 16) | (g << 8) | b;
                pixels[idx++] = v;
                n -= 1;
            }
        } else {
            while(n > 0) {
                int nb = read(buf);
                if ((nb & 0x80) == 0) {
                    for (int i = 0; i <= nb; i++) {
                        int b = read(buf);
                        int g = read(buf);
                        int r = read(buf);
                        pixels[idx++] = 0xff000000 | (r << 16) | (g << 8) | b;
                    }
                } else {
                    nb &= 0x7f;
                    int b = read(buf);
                    int g = read(buf);
                    int r = read(buf);
                    int v = 0xff000000 | (r << 16) | (g << 8) | b;
                    for (int i = 0; i <= nb; i++) {
                        pixels[idx++] = v;
                    }
                }
                n -= nb + 1;
            }
        }

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);

        result = Bitmap.createBitmap(result, 0, 0, width, height, matrix, false);

        return result;
    }

    public static void TGAWriter(Bitmap bitmap, File file) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getRowBytes() * bitmap.getHeight());
        bitmap.copyPixelsToBuffer(buffer);
        boolean alpha = bitmap.hasAlpha();
        byte[] data;

        byte[] pixels = buffer.array();
        if (pixels.length != bitmap.getWidth() * bitmap.getHeight() * (alpha ? 4 : 3))
            throw new IllegalStateException();

        data = new byte[pixels.length];

        for (int i = 0, p = pixels.length - 1; i < data.length; i++, p--) {
            data[i] = pixels[p];
        }

        byte[] header = new byte[18];
        header[2] = 0x02;
        header[12] = (byte) ((bitmap.getWidth() >> 0) & 0xFF);
        header[13] = (byte) ((bitmap.getWidth() >> 8) & 0xFF);
        header[14] = (byte) ((bitmap.getHeight() >> 0) & 0xFF);
        header[15] = (byte) ((bitmap.getHeight() >> 8) & 0xFF);
        header[16] = (byte) (alpha ? 32 : 24);
        header[17] = (byte) ((alpha ? 8 : 0) | (1 << 4));

        byte[] all_buffer = new byte[pixels.length + 18];

        for(int i = 0; i < all_buffer.length; i++) {
            if(i < 18) {
                all_buffer[i] = header[i];
            } else {
                all_buffer[i] = data[i - 18];
            }
        }

        (new File(Environment.getExternalStorageDirectory() + "/mipmap")).mkdirs();

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

        bos.write(all_buffer);
    }
}