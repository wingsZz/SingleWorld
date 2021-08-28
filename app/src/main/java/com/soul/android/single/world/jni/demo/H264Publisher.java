package com.soul.android.single.world.jni.demo;

/**
 * @author : yueqi.zhou
 * @date : 2021-08-21 18:40
 * describe :
 */
public class H264Publisher {

    static {
        System.loadLibrary("native_render");
    }

    public native void initPublish(String url,int width,int height);

    public native void startPublish();

    public native void stopPublish();

    public native void encoderBuffer(byte[] buffers);
}
