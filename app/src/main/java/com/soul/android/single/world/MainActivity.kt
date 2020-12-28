package com.soul.android.single.world

import android.content.Intent
import android.opengl.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.soul.android.single.world.alpha.AlphaMovieActivity
import com.soul.android.single.world.bean.DemoItem
import com.soul.android.single.world.camera.LiveCameraActivity
import com.soul.android.single.world.codec.decode.DecodeActivity
import com.soul.android.single.world.opengl.OpenGLDrawActivity
import com.soul.android.single.world.opengl.fbo.FboActivity
import com.soul.android.single.world.opengl.vao.VaoActivity
import com.soul.android.single.world.opengl.yuv.YUVConvertActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        demoList.layoutManager = GridLayoutManager(this, 2)

        val adapter = DemoItemAdapter()
        demoList.adapter = adapter

        adapter.setNewInstance(fillDemoList())

        adapter.setOnItemClickListener { adapter, view, position ->
            if (position == 0) {
                startActivity(Intent(this@MainActivity, LiveCameraActivity::class.java))
            } else if (position == 1) {
                startActivity(Intent(this@MainActivity, OpenGLDrawActivity::class.java))
            } else if (position == 1) {
                startActivity(Intent(this@MainActivity, AlphaMovieActivity::class.java))
            } else {
                startActivity(Intent(this@MainActivity, FboActivity::class.java))
            }
        }

        lookAt()
    }

    private fun lookAt() {
        val floatArray = FloatArray(16)
        Matrix.setLookAtM(floatArray, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)
        val build = StringBuilder()
        floatArray.forEachIndexed { index, fl ->
            if ((index + 1) % 4 == 0) {
                build.append(floatArray[index]).append("\n")
            } else {
                build.append(floatArray[index]).append(" ")
            }
        }
        Log.e("lookat", build.toString())
    }

    private fun fillDemoList(): MutableList<DemoItem> {
        val demoList = ArrayList<DemoItem>(10)

        val camera = DemoItem(R.drawable.camera, "相机")
        val openGL = DemoItem(R.drawable.camera, "OpenGL")
        val alphaMovie = DemoItem(R.drawable.camera, "Movie")
        val decode = DemoItem(R.drawable.camera, "解码")
        val yuv = DemoItem(R.drawable.camera, "YUV")
        val vao = DemoItem(R.drawable.camera, "VAO&VBO")
        val fbo = DemoItem(R.drawable.camera, "FBO")

        demoList.add(camera)
        demoList.add(openGL)
        demoList.add(alphaMovie)
        demoList.add(decode)
        demoList.add(yuv)
        demoList.add(vao)
        demoList.add(fbo)

        return demoList
    }
}
