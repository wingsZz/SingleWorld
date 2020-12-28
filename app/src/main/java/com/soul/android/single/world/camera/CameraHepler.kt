package com.soul.android.single.world.camera

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display

/**
 * @author : yueqi.zhou
 * @date : 2020-11-20 15:13
 * describe :
 */
class CameraHepler {

    companion object {
        var camera: Camera? = null
        var cameraId = -1
        var width = 0
        var height = 0

        fun openCamera(cameraId: Int, width: Int, height: Int): Int {
            if (camera == null) {
                camera = Camera.open(cameraId)
            }

            if (camera == null) {
                return -1
            }

            this.cameraId = cameraId
            this.height = height
            this.width = width

            val params = camera!!.parameters
            configCamera(cameraId, params, width, height)
            camera!!.parameters = params
            camera!!.setPreviewCallback(object : Camera.PreviewCallback {
                override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
                    Log.e("Camera ","data size = ${data?.size}")
                }

            })

            return 0
        }

        private fun configCamera(
            cameraId: Int,
            params: Camera.Parameters,
            width: Int,
            height: Int
        ) {

            val info = Camera.CameraInfo()
            Camera.getCameraInfo(cameraId, info)
            val sizes = params.supportedPreviewSizes
            sizes.forEachIndexed { index, size ->
                Log.e("Camera", "preview size width = ${size.width},height = ${size.height} \n")
            }
            params.setPreviewSize(1024,768)
        }

        private fun setCameraDisplayOrientation() {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(cameraId, info)
            var degrees = 0
            var result: Int
            Log.e("Camera","orientation = " + info.orientation)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360
                result = (360 - result) % 360 // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360
            }
            Log.e("Camera", "result = $result")
            camera?.setDisplayOrientation(result)
        }

        fun startPreview(surfaceTexture: SurfaceTexture) {
            setCameraDisplayOrientation()
            camera?.setPreviewTexture(surfaceTexture)
            camera?.startPreview()
        }

        fun release() {
            camera?.setPreviewCallback(null)
            camera?.stopPreview()
            camera?.release()
            camera = null
        }
    }
}