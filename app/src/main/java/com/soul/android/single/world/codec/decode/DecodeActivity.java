package com.soul.android.single.world.codec.decode;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.soul.android.single.world.R;
import com.soul.android.single.world.codec.MediaClock;
import com.soul.android.single.world.util.StorageUtil;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-12 22:27
 * describe :
 */
public class DecodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        SurfaceView surfaceView = findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                MediaClock clock = new MediaClock();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MediaDecode decode = new MediaDecode(clock);
                        decode.start(StorageUtil.getVedioPath(true) + "avatar_原声.mp4", surfaceHolder.getSurface());


                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AudioDecode audioDecode = new AudioDecode(clock);
                        audioDecode.start(StorageUtil.getVedioPath(true) + "avatar_原声.mp4");
                    }
                }).start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }
}
