package com.soul.android.single.world.opengl.fbo;

import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.soul.android.single.world.opengl.BaseDraw;
import com.soul.android.single.world.util.GLUtils;

/**
 * @author : yueqi.zhou
 * @date : 2020-11-27 17:38
 * describe :
 */
class FboDraw extends BaseDraw {

    private static final int PIC_WIDTH = 421;
    private static final int PIC_HEIGHT = 586;

    private static final String VERTEX_SHADER =
            "attribute vec4 a_position;   \n" +
                    "attribute vec2 a_texCoord;   \n" +
                    "varying vec2 v_texCoord;                       \n" +
                    "void main()                                \n" +
                    "{                                          \n" +
                    "   gl_Position = a_position;               \n" +
                    "   v_texCoord = a_texCoord;                \n" +
                    "}                                          \n";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying vec2 v_texCoord;\n" +
                    "uniform sampler2D s_TextureMap;\n" +
                    "void main()\n" +
                    "{\n" +
                    "    gl_FragColor = texture2D(s_TextureMap, v_texCoord);\n" +
                    "}";

    private static final String FBO_FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying vec2 v_texCoord;\n" +
                    "uniform sampler2D s_TextureMap;\n" +
                    "void main()\n" +
                    "{\n" +
                    "    vec4 tempColor = texture2D(s_TextureMap, v_texCoord);\n" +
                    "    float luminance = tempColor.r * 0.299 + tempColor.g * 0.587 + tempColor.b * 0.114;\n" +
                    "    gl_FragColor = vec4(vec3(luminance), tempColor.a);\n" +
                    "}";


    private static final float[] VERTEX_DATA = new float[]{
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f,  1.0f, 0.0f,
            1.0f,  1.0f, 0.0f,
    };

    private static final float[] FBO_TEXTURE_COORD = new float[]{
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };

    private static final float[] TEXTURE_COORD = new float[]{
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    private static final short[] INDICES = new short[]{0, 1, 2, 1, 3, 2};

    private byte[] datas;

    private int fboId;
    private int fboTextureId;
    private int fboProgram;

    private int textureId;


    private int vertexBuffer;
    private int textureCoordBuffer;
    private int fboTextureCoordBuffer;
    private int indexBuffer;

    private int textureHandler;
    private int fboTextureHandler;

    public FboDraw(byte[] datas) {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
        this.datas = datas;
    }

    @Override
    protected void onSurfaceCreated() {
        super.onSurfaceCreated();


        // 创建fbo program
        int fboVertexShader = GLUtils.loadShader(VERTEX_SHADER, GLES20.GL_VERTEX_SHADER);
        int fboFragmentShader = GLUtils.loadShader(FBO_FRAGMENT_SHADER, GLES20.GL_FRAGMENT_SHADER);
        fboProgram = GLUtils.createProgram(new int[]{fboVertexShader, fboFragmentShader});

        genFBO();
        genNormalTexture();
        bindVertexBuffer();

        textureHandler = GLUtils.getLocation(GLUtils.PARAMS_TYPE_UNIFORM, program, "s_TextureMap");
        fboTextureHandler = GLUtils.getLocation(GLUtils.PARAMS_TYPE_UNIFORM, fboProgram, "s_TextureMap");
    }

    private void genFBO() {
        // 生成一个纹理绑定到fbo
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLUtils.checkGlError("glGenTextures");
        fboTextureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);

        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        GLUtils.checkGlError("glGenFramebuffers");
        fboId = frameBuffers[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureId);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTextureId, 0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, PIC_WIDTH, PIC_HEIGHT, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("create fbo failed!");
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    private void genNormalTexture() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLUtils.checkGlError("glGenTextures");
        textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,PIC_WIDTH,PIC_HEIGHT,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,GLUtils.createByteBuffer(datas));
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);
    }

    private void bindVertexBuffer() {

//        int[] buffers = new int[4];
//        GLES20.glGenBuffers(4, buffers, 0);

        // 顶点数组buffer
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
//        vertexBuffer = buffers[0];
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VERTEX_DATA.length * 4, GLUtils.createFloatBuffer(VERTEX_DATA), GLES20.GL_STATIC_DRAW);



//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // 普通纹理buffer
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
//        textureCoordBuffer = buffers[1];
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, TEXTURE_COORD.length * 4, GLUtils.createFloatBuffer(TEXTURE_COORD), GLES20.GL_STATIC_DRAW);

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // fbo纹理buffer
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
//        fboTextureCoordBuffer = buffers[2];
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, FBO_TEXTURE_COORD.length * 4, GLUtils.createFloatBuffer(FBO_TEXTURE_COORD), GLES20.GL_STATIC_DRAW);

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // 索引buffer
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[3]);
//        indexBuffer = buffers[3];
//        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, INDICES.length * 2, GLUtils.createShortBuffer(INDICES), GLES20.GL_STATIC_DRAW);
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
    }

    @Override
    protected void onDraw() {
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glViewport(0, 0, PIC_WIDTH, PIC_HEIGHT);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, fboTextureCoordBuffer);
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        GLES20.glUseProgram(fboProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(textureHandler, 0);

        int fboPositionHandler = GLUtils.getLocation(GLUtils.PARAMS_TYPE_ATTRIBUTE, fboProgram, "a_position");
        int fboTextureCoordHandler = GLUtils.getLocation(GLUtils.PARAMS_TYPE_ATTRIBUTE, fboProgram, "a_texCoord");
        GLES20.glEnableVertexAttribArray(fboPositionHandler);
        GLES20.glVertexAttribPointer(fboPositionHandler, 3, GLES20.GL_FLOAT, false, 3 * 4, GLUtils.createFloatBuffer(VERTEX_DATA));
        GLES20.glEnableVertexAttribArray(fboTextureCoordHandler);
        GLES20.glVertexAttribPointer(fboTextureCoordHandler, 2, GLES20.GL_FLOAT, false, 2 * 4, GLUtils.createFloatBuffer(FBO_TEXTURE_COORD));
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, GLUtils.createShortBuffer(INDICES));
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
////        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
////        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
////        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureCoordBuffer);
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        GLES20.glViewport(0, 0, 1080, 1920);
        GLES20.glUseProgram(program);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureId);
        GLES20.glUniform1i(fboTextureHandler, 0);
        int positionHandler = GLUtils.getLocation(GLUtils.PARAMS_TYPE_ATTRIBUTE, program, "a_position");
        int textureCoordHandler = GLUtils.getLocation(GLUtils.PARAMS_TYPE_ATTRIBUTE, program, "a_texCoord");
        GLES20.glEnableVertexAttribArray(positionHandler);
        GLES20.glVertexAttribPointer(positionHandler, 3, GLES20.GL_FLOAT, false, 3 * 4, GLUtils.createFloatBuffer(VERTEX_DATA));
        GLES20.glEnableVertexAttribArray(textureCoordHandler);
        GLES20.glVertexAttribPointer(textureCoordHandler, 2, GLES20.GL_FLOAT, false, 2 * 4, GLUtils.createFloatBuffer(TEXTURE_COORD));
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, GLUtils.createShortBuffer(INDICES));
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
