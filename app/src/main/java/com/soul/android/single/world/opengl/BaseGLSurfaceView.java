package com.soul.android.single.world.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.soul.android.single.world.opengl.BaseDraw;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-25 20:05
 * describe :
 */
public class BaseGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private BaseDraw draw;

    public BaseGLSurfaceView(Context context, BaseDraw draw) {
        super(context);
        this.draw = draw;
        init();
    }

    public BaseGLSurfaceView(Context context, AttributeSet attrs, BaseDraw draw) {
        super(context, attrs);
        this.draw = draw;
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (draw != null) {
            draw.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (draw != null) {
            draw.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (draw != null) {
            draw.onDraw();
        }
    }
}
