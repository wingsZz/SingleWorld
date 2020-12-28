package com.soul.android.single.world.opengl.yuv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-23 15:03
 * describe :
 */
public class YUVConvertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        new Thread(() -> {
            BufferedInputStream inputStream;
            try {

                inputStream = new BufferedInputStream(getAssets().open("yuv/YUV_Image_840x1074.NV21"));
                byte[] bytes = new byte[inputStream.available()];
                while (inputStream.read(bytes) > 0) {
                    Log.e("YUV", "read byte");
                }

                runOnUiThread(() -> {
                    NV21Image image = new NV21Image(840, 1074, bytes);
                    YUVGLSurfaceView surfaceView = new YUVGLSurfaceView(YUVConvertActivity.this, image);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    addContentView(surfaceView, params);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public static Bitmap nv12ToBitmap(byte[] data, int w, int h) {
        return spToBitmap(data, w, h, 0, 1);
    }

    public static Bitmap nv21ToBitmap(byte[] data, int w, int h) {
        return spToBitmap(data, w, h, 1, 0);
    }

    private static Bitmap spToBitmap(byte[] data, int w, int h, int uOff, int vOff) {
        int plane = w * h;
        int[] colors = new int[plane];
        int yPos = 0, uvPos = plane;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                // YUV byte to RGB int
                final int y1 = data[yPos] & 0xff;
                final int u = (data[uvPos + uOff] & 0xff) - 128;
                final int v = (data[uvPos + vOff] & 0xff) - 128;
                final int y1192 = 1192 * y1;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                r = (r < 0) ? 0 : ((r > 262143) ? 262143 : r);
                g = (g < 0) ? 0 : ((g > 262143) ? 262143 : g);
                b = (b < 0) ? 0 : ((b > 262143) ? 262143 : b);
                colors[yPos] = ((r << 6) & 0xff0000) |
                        ((g >> 2) & 0xff00) |
                        ((b >> 10) & 0xff);

                if ((yPos++ & 1) == 1) uvPos += 2;
            }
            if ((j & 1) == 0) uvPos -= w;
        }
        return Bitmap.createBitmap(colors, w, h, Bitmap.Config.RGB_565);
    }

    public static Bitmap i420ToBitmap(byte[] data, int w, int h) {
        return pToBitmap(data, w, h, true);
    }

    public static Bitmap yv12ToBitmap(byte[] data, int w, int h) {
        return pToBitmap(data, w, h, false);
    }

    private static Bitmap pToBitmap(byte[] data, int w, int h, boolean uv) {
        int plane = w * h;
        int[] colors = new int[plane];
        int off = plane >> 2;
        int yPos = 0, uPos = plane + (uv ? 0 : off), vPos = plane + (uv ? off : 0);
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                // YUV byte to RGB int
                final int y1 = data[yPos] & 0xff;
                final int u = (data[uPos] & 0xff) - 128;
                final int v = (data[vPos] & 0xff) - 128;
                final int y1192 = 1192 * y1;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                r = (r < 0) ? 0 : ((r > 262143) ? 262143 : r);
                g = (g < 0) ? 0 : ((g > 262143) ? 262143 : g);
                b = (b < 0) ? 0 : ((b > 262143) ? 262143 : b);
                colors[yPos] = ((r << 6) & 0xff0000) |
                        ((g >> 2) & 0xff00) |
                        ((b >> 10) & 0xff);

                if ((yPos++ & 1) == 1) {
                    uPos++;
                    vPos++;
                }
            }
            if ((j & 1) == 0) {
                uPos -= (w >> 1);
                vPos -= (w >> 1);
            }
        }
        return Bitmap.createBitmap(colors, w, h, Bitmap.Config.RGB_565);
    }
}
