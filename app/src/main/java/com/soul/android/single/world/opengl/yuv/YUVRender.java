package com.soul.android.single.world.opengl.yuv;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-24 17:47
 * describe :
 */
class YUVRender implements GLSurfaceView.Renderer {
    private YUVDrawer drawer;

    public YUVRender(NV21Image image) {
        drawer = new YUVDrawer(image);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        drawer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        drawer.onSurfaceChanged(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawer.onDraw();
    }
}
