package com.soul.android.single.world.camera.config.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.soul.android.single.world.R;
import com.soul.android.single.world.camera.config.CameraConfig;

/**
 * @author : yueqi.zhou
 * @date : 2020-12-01 21:33
 * describe :
 */
public class CameraControllerView extends ConstraintLayout implements View.OnClickListener {

    private CameraConfig config;

    private ImageView switchCamera;
    private ImageView switchFlash;
    private ImageView switchProportion;

    public CameraControllerView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CameraControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CameraControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public CameraControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context){
        View view = View.inflate(context, R.layout.widget_camera_controller,null);
        switchCamera = view.findViewById(R.id.switchCamera);
        switchFlash = view.findViewById(R.id.switchFlash);
        switchProportion = view.findViewById(R.id.switchProportion);

        switchCamera.setOnClickListener(this);
        switchFlash.setOnClickListener(this);
        switchProportion.setOnClickListener(this);
    }

    public void bindCameraConfig(CameraConfig config){
        this.config = config;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.switchCamera:
                break;
            case R.id.switchFlash:
                break;
            case R.id.switchProportion:
                break;
        }
    }
}
