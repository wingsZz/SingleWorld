package com.soul.android.single.world.opengl

import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author : yueqi.zhou
 * @date : 2020-11-22 22:41
 * describe :
 */
class GLRenderer : GLSurfaceView.Renderer {

    lateinit var triangle:Triangle
    val mvp = FloatArray(16)


    override fun onDrawFrame(gl: GL10?) {
        triangle.draw(mvp)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        triangle = Triangle()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Matrix.setIdentityM(mvp, 0)
    }
}