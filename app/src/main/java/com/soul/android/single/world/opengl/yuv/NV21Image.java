package com.soul.android.single.world.opengl.yuv;

import java.io.Serializable;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-24 20:54
 * describe :
 */
class NV21Image implements Serializable {
    public int width;
    public int height;
    public byte[] datas;

    public NV21Image(int width, int height, byte[] datas) {
        this.width = width;
        this.height = height;
        this.datas = datas;
    }
}
