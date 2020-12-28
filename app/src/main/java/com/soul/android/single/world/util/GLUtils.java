package com.soul.android.single.world.util;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-16 21:16
 * describe :
 */
public class GLUtils {
    private static final String TAG = GLUtils.class.getSimpleName();

    public static final int PARAMS_TYPE_ATTRIBUTE = 0;
    public static final int PARAMS_TYPE_UNIFORM = 1;

    public static int loadShader(String shaderSource, int shaderType) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader <= 0) {
            throw new RuntimeException("Create shader failed,please checkout your egl");
        }

        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static int createProgram(int[] shaders) {
        int program = GLES20.glCreateProgram();
        if (program <= 0) {
            throw new RuntimeException("Create program failed,please checkout your egl");
        }
        if (shaders.length > 0) {
            for (int i = 0; i < shaders.length; i++) {
                GLES20.glAttachShader(program, shaders[i]);
            }
        }

        GLES20.glLinkProgram(program);

        return program;
    }

    public static int getLocation(int paramsType, int program, String paramsName) {
        if (paramsType == PARAMS_TYPE_ATTRIBUTE) {
            return GLES20.glGetAttribLocation(program, paramsName);
        } else if (paramsType == PARAMS_TYPE_UNIFORM) {
            return GLES20.glGetUniformLocation(program, paramsName);
        } else {
            throw new IllegalArgumentException("Unsupport paramsType,please check!");
        }
    }

    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + " Error -- " + Integer.toHexString(error));
        }
    }

    public static FloatBuffer createFloatBuffer(float[] floatArray) {
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(floatArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(floatArray);
        floatBuffer.position(0);

        return floatBuffer;
    }

    public static ShortBuffer createShortBuffer(short[] floatArray) {
        ShortBuffer floatBuffer = ByteBuffer.allocateDirect(floatArray.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        floatBuffer.put(floatArray);
        floatBuffer.position(0);

        return floatBuffer;
    }

    public static ByteBuffer createByteBuffer(byte[] datas){
        ByteBuffer floatBuffer = ByteBuffer.allocateDirect(datas.length).order(ByteOrder.nativeOrder());
        floatBuffer.put(datas);
        floatBuffer.position(0);

        return floatBuffer;
    }

    public static void logTransformMatrix(float[] matrix) {
        StringBuilder stringBuffer = new StringBuilder("\n");
        if (matrix == null || matrix.length == 0) {
            return;
        }

        for (int i = 0; i < matrix.length; i++) {
            stringBuffer.append(matrix[i]).append(" ");
            if ((i + 1) % 4 == 0) {
                stringBuffer.append("\n");
            }
        }
        Log.e(TAG, "transform =  " + stringBuffer);
    }
}
