package com.soul.android.single.world.bean;

import androidx.annotation.DrawableRes;

import java.io.Serializable;

/**
 * @author : yueqi.zhou
 * @date : 2020-10-11 15:50
 * describe :
 */
public class DemoItem implements Serializable {
    @DrawableRes
    public int iconRes;
    public String name;

    public DemoItem(int iconRes, String name) {
        this.iconRes = iconRes;
        this.name = name;
    }
}
