package com.soul.android.single.world.jni.demo;

/**
 * @author : yueqi.zhou
 * @date : 2021-08-21 16:21
 * describe :
 */
public class DemoJNIClass {
    static {
        System.loadLibrary("native_render");
    }


    public native String nativeGetString();
}
