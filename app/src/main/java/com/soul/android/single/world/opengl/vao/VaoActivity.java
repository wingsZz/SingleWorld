package com.soul.android.single.world.opengl.vao;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.soul.android.single.world.opengl.BaseGLSurfaceView;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-25 19:46
 * describe :
 */
public class VaoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VaoDraw draw = new VaoDraw();
        BaseGLSurfaceView surfaceView = new BaseGLSurfaceView(this, draw);
        addContentView(surfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
