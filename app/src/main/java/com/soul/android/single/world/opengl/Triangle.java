package com.soul.android.single.world.opengl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;

import com.soul.android.single.world.util.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author : yueqi.zhou
 * @date : 2020-10-12 15:04
 * describe :
 */
public class Triangle {

    /**
     * 顶点着色器
     */
    private static final String VERTEX_SHADER_CODE =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "uniform mat4 uTextureMatrix;" +
                    "attribute vec4 aPosition;" +
                    "attribute vec4 aTextureCoord;" +
                    "varying vec2 vTextureCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * aPosition;" +
                    "  vTextureCoord = (uTextureMatrix * aTextureCoord).xy;" +
                    "}";


    /**
     * 片段着色器
     */
    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform sampler2D sTexture;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(sTexture,vTextureCoord);" +
                    "}";

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int program;

    /**
     * 因为是三维坐标，每个顶点在坐标系使用3个数字
     */
    private static final int COORDS_PER_VERTEX = 3;

    /**
     * 三角形三个顶点的坐标(X,Y,Z)
     * 这里定义了一个等边三角形的三个顶点，如果没有特殊处理，显示在屏幕上的将会是一个等腰三角形而不是等边，
     * 这是因为在OpenGL的标准坐标系中定义的顶点坐标会映射到屏幕坐标中，这个映射过程就使得我们的实际绘制图案和预期产生了偏差。
     */
    private float[] triangleCoords = new float[]{
            0.0f, 0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f
    };

    private float[] colorCoords = new float[]{
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f
    };

    private float[] tMatrix = new float[16];

    /**
     * 绘图时使用的形状(argb)
     * color[0] = RED
     * color[1] = GREEN
     * color[2] = RED
     * color[1] = ALPHA
     */
    private float[] color = new float[]{0.63671875f, 0.76953125f, 0.22265625f, 1.0f};


    private int positionHandle;
    private int textureCoordHandle;
    // Use to access and set the view transformation
    private int vPMatrixHandle;

    private int textureMatrixHandle;


    private int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private int vertexStride = COORDS_PER_VERTEX * 4;

    private int[] textureIds = new int[1];

    public Triangle() {
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(colorCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(colorCoords);
        textureBuffer.position(0);

        // 初始化着色器代码
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);

        program = GLES20.glCreateProgram();
        genCustomTexture();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }


    public void draw(float[] mvpMatrix) {
        Matrix.setIdentityM(tMatrix, 0);

        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(program);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        GLUtils.checkGlError("glGetAttribLocation");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLUtils.checkGlError("glEnableVertexAttribArray");
        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
        );
        GLUtils.checkGlError("glVertexAttribPointer");


        textureCoordHandle = GLES20.glGetAttribLocation(program, "aTextureCoord");
        GLUtils.checkGlError("glGetAttribLocation");
        GLES20.glEnableVertexAttribArray(textureCoordHandle);
        GLES20.glVertexAttribPointer(
                textureCoordHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                8,
                textureBuffer
        );
        GLUtils.checkGlError("glUniform4fv");

        vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLUtils.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        GLUtils.checkGlError("glUniformMatrix4fv");

        textureMatrixHandle = GLES20.glGetUniformLocation(program, "uTextureMatrix");
        GLUtils.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(textureMatrixHandle, 1, false, tMatrix, 0);
        GLUtils.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLUtils.checkGlError("glDrawArrays");
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLUtils.checkGlError("glDisableVertexAttribArray");
        GLES20.glDisableVertexAttribArray(textureCoordHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private void genCustomTexture() {
        // 生成一个新的纹理
        GLES20.glGenTextures(1, textureIds, 0);
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        // 设置环绕模式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // 设置过滤模式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/test.png");
        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        bitmap.recycle();

    }
}
