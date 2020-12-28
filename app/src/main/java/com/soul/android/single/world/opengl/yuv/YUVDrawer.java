package com.soul.android.single.world.opengl.yuv;

import android.opengl.GLES20;

import com.soul.android.single.world.util.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-24 17:53
 * describe :
 */
class YUVDrawer {

    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    vTextureCoord = aTextureCoord;\n" +
                    "    gl_Position = aPosition;\n" +
                    "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D y_texture;\n" +
                    "uniform sampler2D uv_texture;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec3 yuv;\n" +
                    "    yuv.x = texture2D(y_texture, vTextureCoord).r;\n" +
                    "    yuv.y = texture2D(uv_texture, vTextureCoord).a - 0.5;\n" +
                    "    yuv.z = texture2D(uv_texture, vTextureCoord).r - 0.5;\n" +
                    "\n" +
                    "    vec3 rgb = mat3(1.0, 1.0, 1.0,\n" +
                    "    0.0, -0.344, 1.770,\n" +
                    "    1.403, -0.714, 0.0) * yuv;\n" +
                    "    gl_FragColor = vec4(rgb, 1);\n" +
                    "}";

    private static final float[] VERTEX_COORDS = new float[]{
            -1.0f, 0.78f, 0.0f,  // Position 0
            -1.0f, -0.78f, 0.0f,  // Position 1
            1.0f, -0.78f, 0.0f,  // Position 2
            1.0f, 0.78f, 0.0f,  // Position 3
    };

    private static final float[] FRAGGMENT_COORDS = new float[]{
            0.0f, 0.0f,        // TexCoord 0
            0.0f, 1.0f,        // TexCoord 1
            1.0f, 1.0f,        // TexCoord 2
            1.0f, 0.0f         // TexCoord 3
    };

    private static final short[] indices = new short[]{0, 1, 2, 0, 2, 3};

    private int yTexture;
    private int uvTexture;

    private int program;
    private int positionHandler;
    private int textureCoordHandler;
    private int yTextureHandler;
    private int uvTextureHandler;

    private NV21Image image;
    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmentBuffer;
    private ShortBuffer indicesBuffer;

    public YUVDrawer(NV21Image image) {
        this.image = image;

        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_COORDS.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(VERTEX_COORDS).position(0);
        fragmentBuffer = ByteBuffer.allocateDirect(FRAGGMENT_COORDS.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fragmentBuffer.put(FRAGGMENT_COORDS).position(0);
        indicesBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
    }

    public void onSurfaceCreated() {
        int[] shaders = new int[2];
        int vertexShader = GLUtils.loadShader(VERTEX_SHADER, GLES20.GL_VERTEX_SHADER);
        shaders[0] = vertexShader;
        int fragmentShader = GLUtils.loadShader(FRAGMENT_SHADER, GLES20.GL_FRAGMENT_SHADER);
        shaders[1] = fragmentShader;

        program = GLUtils.createProgram(shaders);

        positionHandler = GLES20.glGetAttribLocation(program, "aPosition");
        GLUtils.checkGlError("glGetAttribLocation");
        textureCoordHandler = GLES20.glGetAttribLocation(program, "aTextureCoord");
        GLUtils.checkGlError("glGetAttribLocation");
        yTextureHandler = GLES20.glGetUniformLocation(program, "y_texture");
        GLUtils.checkGlError("yTextureHandler");
        uvTextureHandler = GLES20.glGetUniformLocation(program, "uv_texture");
        GLUtils.checkGlError("uvTextureHandler");

        createTexture();
    }

    public void onDraw() {
        GLES20.glUseProgram(program);

        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTexture);
        // 这里第二个参数指定的是纹理单元，GL_TEXTUREI = I
        GLES20.glUniform1i(yTextureHandler, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTexture);
        GLES20.glUniform1i(uvTextureHandler, 1);

        GLES20.glEnableVertexAttribArray(positionHandler);
        GLUtils.checkGlError("glEnableVertexAttribArray");
        GLES20.glVertexAttribPointer(positionHandler, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLUtils.checkGlError("glVertexAttribPointer");

        GLES20.glEnableVertexAttribArray(textureCoordHandler);
        GLUtils.checkGlError("glEnableVertexAttribArray");
        GLES20.glVertexAttribPointer(textureCoordHandler, 2, GLES20.GL_FLOAT, false, 2 * 4, fragmentBuffer);
        GLUtils.checkGlError("glVertexAttribPointer");

        //TODO 如果这里的type指定为GL_SHORT的话 会报错 但是不知道为什么
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
        GLUtils.checkGlError("glDrawElements");

        GLES20.glDisableVertexAttribArray(positionHandler);
        GLES20.glDisableVertexAttribArray(textureCoordHandler);
        GLES20.glUseProgram(0);
    }

    private void createTexture() {
        int[] textures = new int[2];
        GLES20.glGenTextures(2, textures, 0);

        yTexture = textures[0];
        uvTexture = textures[1];

        ByteBuffer yBuffer = ByteBuffer.allocateDirect(image.width * image.height).order(ByteOrder.nativeOrder());
        yBuffer.put(Arrays.copyOf(image.datas, image.width * image.height));
        yBuffer.position(0);

        ByteBuffer uvBuffer = ByteBuffer.allocateDirect(image.width * image.height / 2).order(ByteOrder.nativeOrder());
        uvBuffer.put(Arrays.copyOfRange(image.datas, image.width * image.height, image.datas.length));
        uvBuffer.position(0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTexture);
        GLUtils.checkGlError("glBindTexturex");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.checkGlError("glTexParameterf");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLUtils.checkGlError("glTexParameterf");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.checkGlError("glTexParameterf");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.checkGlError("glTexParameterf");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, image.width, image.height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yBuffer);
        GLUtils.checkGlError("glTexImage2D");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLUtils.checkGlError("glBindTexture");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uvTexture);
        GLUtils.checkGlError("glBindTexture");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.checkGlError("glTexParameterf");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLUtils.checkGlError("glTexParameterf");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.checkGlError("glTexParameterf");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.checkGlError("glTexParameterf");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, image.width >> 1, image.height >> 1, 0, GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, uvBuffer);
        GLUtils.checkGlError("glTexImage2D");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLUtils.checkGlError("glBindTexture");
    }

    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
}
