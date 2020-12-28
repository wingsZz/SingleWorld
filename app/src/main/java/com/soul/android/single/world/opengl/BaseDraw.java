package com.soul.android.single.world.opengl;

import android.opengl.GLES20;

import com.soul.android.single.world.util.GLUtils;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-25 19:59
 * describe :
 */
public abstract class BaseDraw {
    private static final String VERTEX_SHADER = "";
    private static final String FRAGMENT_SHADER = "";

    protected int vertexShader;
    protected int fragmentShader;
    protected int program;
    protected String vertexShaderSource = VERTEX_SHADER;
    protected String fragmentShaderSource = FRAGMENT_SHADER;

    public BaseDraw() {

    }

    public BaseDraw(String vertexShaderSource, String fragmentShaderSource) {
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;
    }

    protected void onSurfaceCreated() {
        vertexShader = GLUtils.loadShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER);
        fragmentShader = GLUtils.loadShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER);

        program = GLUtils.createProgram(new int[]{vertexShader, fragmentShader});
    }

    protected abstract void onSurfaceChanged(int width, int height);

    protected abstract void onDraw();

}
