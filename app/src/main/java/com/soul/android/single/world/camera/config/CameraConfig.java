package com.soul.android.single.world.camera.config;

import android.hardware.Camera;

import java.io.Serializable;

/**
 * @author : yueqi.zhou
 * @date : 2020-12-01 21:27
 * describe :
 */
public class CameraConfig implements Serializable {
    public float proportion = PreviewProportion.PROPORTION_16_9;
    public int flashMode = FlashMode.MODE_AUTO;
    public int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

}
