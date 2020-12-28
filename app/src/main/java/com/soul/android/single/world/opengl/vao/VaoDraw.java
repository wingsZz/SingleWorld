package com.soul.android.single.world.opengl.vao;

import android.opengl.GLES20;

import com.soul.android.single.world.opengl.BaseDraw;
import com.soul.android.single.world.util.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-25 20:10
 * describe :
 */
class VaoDraw extends BaseDraw {

    private static final String VERTEX_SHADER = "attribute vec4 aPosition;\n" +
            "attribute vec4 aColor;\n" +
            "varying vec4 outPosition;\n" +
            "varying vec4 outColor;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = aPosition;\n" +
            "    outPosition = aPosition;\n" +
            "    outColor = aColor;\n" +
            "}";

    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "varying vec4 outColor;\n" +
            "varying vec4 outPosition;\n" +
            "\n" +
            "void main() {\n" +
            "    float n = 10.0;\n" +
            "    float span = 1.0/n;\n" +
            "    int i = int((outPosition.x + 0.5)/span);\n" +
            "    int j = int((outPosition.y + 0.5)/span);\n" +
            "\n" +
            "    int grayColor = int(mod(float(i+j), 2.0));\n" +
            "    if (grayColor == 1){\n" +
            "        float luminance = outColor.r*0.299 + outColor.g*0.587 + outColor.b*0.114;\n" +
            "        gl_FragColor = vec4(vec3(luminance), outColor.a);\n" +
            "    } else {\n" +
            "        gl_FragColor = outColor;\n" +
            "    }\n" +
            "}";

    private static final float[] VERTEX_DATA = {
            -0.5f, 0.5f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.0f,
            0.5f, 1.0f, 1.0f, 1.0f
    };

    private static final short[] INDICES = new short[]{0, 1, 2, 0, 2, 3};


    private int positionHandler;
    private int colorHandler;

    VaoDraw() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    protected void onSurfaceCreated() {
        super.onSurfaceCreated();
        int[] buffers = new int[2];
        GLES20.glGenBuffers(2, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);

        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(VERTEX_DATA.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(VERTEX_DATA).position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VERTEX_DATA.length * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLUtils.checkGlError("glBufferData");

        ShortBuffer indicesBuffer = ByteBuffer.allocateDirect(INDICES.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(INDICES).position(0);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, INDICES.length * 2, indicesBuffer, GLES20.GL_STATIC_DRAW);
        GLUtils.checkGlError("glBufferData");

        positionHandler = GLES20.glGetAttribLocation(program, "aPosition");
        GLUtils.checkGlError("glGetAttribLocation");
        colorHandler = GLES20.glGetAttribLocation(program, "aColor");
        GLUtils.checkGlError("glGetAttribLocation");
        GLES20.glEnableVertexAttribArray(positionHandler);
        GLES20.glEnableVertexAttribArray(colorHandler);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
    }

    @Override
    protected void onDraw() {
        GLES20.glUseProgram(program);

        GLES20.glVertexAttribPointer(positionHandler, 3, GLES20.GL_FLOAT, false, 7 * 4, 0);
        GLES20.glVertexAttribPointer(colorHandler, 4, GLES20.GL_FLOAT, false, 7 * 4, 3 * 4);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, 0);
    }
}
