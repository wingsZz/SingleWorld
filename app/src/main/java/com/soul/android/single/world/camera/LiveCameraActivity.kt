package com.soul.android.single.world.camera

import android.hardware.Camera
import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.soul.android.single.world.R
import com.soul.android.single.world.util.PermissionHelper
import kotlinx.android.synthetic.main.act_camera.*


/**
 * @author : yueqi.zhou
 * @date : 2020-10-11 16:04
 * describe :
 */
class LiveCameraActivity : AppCompatActivity() {

    private var glSurfaceView:CameraGLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_camera)

    }

    override fun onResume() {
        /**
         * 这个方法返回的角度是补偿角度
         * 什么是补偿角度呢？
         * 我们定义手机的自然方向是竖直方向(即宽>高的状态)
         * 当我们逆时针转动手机到水平方向，为了保证手机看到的画面仍然是正常的，渲染画面的时候就会顺时针旋转90度，来做一个补偿，是手机看到的画面仍然是正常的
         */
        Log.e("Camera","degress = " + (getSystemService(WINDOW_SERVICE) as WindowManager).getDefaultDisplay().getRotation())
        super.onResume()
        if (PermissionHelper.hasCameraPermission(this)) {
            startPreview()
        } else {
            PermissionHelper.requestCameraPermission(this, true)
        }
    }

    private fun startPreview(){
        glSurfaceView = CameraGLSurfaceView(this)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        root.addView(glSurfaceView, params)
        CameraHepler.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, 1080, 1920)
    }

    override fun onPause() {
        super.onPause()
        CameraHepler.release()
        root.removeView(glSurfaceView)
        glSurfaceView = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!PermissionHelper.hasCameraPermission(this)) {
            finish()
        } else {
           startPreview()
        }
    }
}