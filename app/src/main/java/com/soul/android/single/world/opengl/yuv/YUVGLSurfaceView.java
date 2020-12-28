package com.soul.android.single.world.opengl.yuv;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-24 17:48
 * describe :
 */
class YUVGLSurfaceView extends GLSurfaceView {
    public YUVGLSurfaceView(Context context, NV21Image image) {
        super(context);
        init(image);
    }

    public YUVGLSurfaceView(Context context, AttributeSet attrs, NV21Image image) {
        super(context, attrs);
        init(image);
    }

    private void init(NV21Image image) {
        setEGLContextClientVersion(2);
        setRenderer(new YUVRender(image));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
