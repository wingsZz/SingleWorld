package com.soul.android.single.world.opengl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author : yueqi.zhou
 * @date : 2020-10-12 15:44
 * describe :
 */
class OpenGLDrawActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        val renderer = GLRenderer()
        val gLView = MyGLSurfaceView(this, renderer)
        setContentView(gLView)
    }
}