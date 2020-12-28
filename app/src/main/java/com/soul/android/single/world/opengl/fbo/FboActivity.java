package com.soul.android.single.world.opengl.fbo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.soul.android.single.world.opengl.BaseGLSurfaceView;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-27 17:37
 * describe :
 */
public class FboActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(() -> {
            BufferedInputStream inputStream;
            try {

                inputStream = new BufferedInputStream(getAssets().open("pic/fbo.jpg"));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    int bytes = bitmap.getByteCount();
                    ByteBuffer buf = ByteBuffer.allocate(bytes);
                    bitmap.copyPixelsToBuffer(buf);
                    byte[] byteArray = buf.array();
                    runOnUiThread(() -> {
                        FboDraw draw = new FboDraw(byteArray);
                        BaseGLSurfaceView surfaceView = new BaseGLSurfaceView(this, draw);
                        addContentView(surfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}
