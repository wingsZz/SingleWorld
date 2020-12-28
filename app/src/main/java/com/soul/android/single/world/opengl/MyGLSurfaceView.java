package com.soul.android.single.world.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author : yueqi.zhou
 * @date : 2020-10-12 15:05
 * describe :
 */
public class MyGLSurfaceView extends GLSurfaceView {
    private GLRenderer renderer;

    public MyGLSurfaceView(Context context, GLRenderer renderer) {
        super(context);
        init(renderer);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs, GLRenderer renderer) {
        super(context, attrs);
        init(renderer);
    }


    private void init(GLRenderer renderer) {
        this.renderer = renderer;
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
