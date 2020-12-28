package com.soul.android.single.world.camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import com.soul.android.single.world.util.GLUtils
import java.lang.ref.WeakReference
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author : yueqi.zhou
 * @date : 2020-11-20 14:33
 * describe :
 */
class CameraGLSurfaceView : GLSurfaceView, SurfaceTexture.OnFrameAvailableListener {
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    private fun init() {
        setEGLContextClientVersion(2)
        setRenderer(CameraRenderer(this))
        renderMode = RENDERMODE_WHEN_DIRTY
    }


    private class CameraRenderer : Renderer {
        private var textureId = 0
        private lateinit var surfaceTexture: SurfaceTexture
        private lateinit var programOES: ProgramTextureOES
        private var glSurfaceView: WeakReference<CameraGLSurfaceView>

        constructor(surfaceView: CameraGLSurfaceView) {
            glSurfaceView = WeakReference(surfaceView)
        }


        override fun onDrawFrame(gl: GL10?) {

            // Latch the latest frame.  If there isn't anything new, we'll just re-use whatever
            // was there before.
            surfaceTexture.updateTexImage()
            surfaceTexture.getTransformMatrix(programOES.stMatrix)
            GLUtils.logTransformMatrix(programOES.stMatrix)
            programOES.drawFrame()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            programOES = ProgramTextureOES()
            surfaceTexture = SurfaceTexture(programOES.textureId)
            surfaceTexture.setOnFrameAvailableListener(glSurfaceView.get())
            CameraHepler.startPreview(surfaceTexture)
        }
    }


    private class ProgramTextureOES {
        companion object {
            val VERTEX_SHADER =
                "// 试图变换矩阵\n" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "// 纹理变换矩阵\n" +
                        "uniform mat4 uSTMatrix;\n" +
                        "// 视图坐标\n" +
                        "attribute vec4 aPosition;\n" +
                        "// 纹理坐标\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "// 变换之后输出的纹理坐标\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "    // 因为这里使用的2D纹理，最终使用到的坐标是一个二维坐标\n" +
                        "    vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                        "}"
            val FRAGMENT_SHADER =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES texture;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(texture, vTextureCoord);\n" +
                        "}"

            private val FULL_RECTANGLE_COORDS = floatArrayOf(
                -1.0f, -1.0f,  // 0 bottom left
                1.0f, -1.0f,  // 1 bottom right
                -1.0f, 1.0f,  // 2 top left
                1.0f, 1.0f
            )
            private val FULL_RECTANGLE_TEX_COORDS = floatArrayOf(
                0.0f, 0.0f,  // 0 bottom left
                1.0f, 0.0f,  // 1 bottom right
                0.0f, 1.0f,  // 2 top left
                1.0f, 1.0f // 3 top right
            )

            private val FULL_RECTANGLE_BUF: FloatBuffer =
                GLUtils.createFloatBuffer(FULL_RECTANGLE_COORDS)
            private val FULL_RECTANGLE_TEX_BUF: FloatBuffer =
                GLUtils.createFloatBuffer(FULL_RECTANGLE_TEX_COORDS)
        }

        private var progrom = 0
        var textureId = 0

        // 纹理变换矩阵
        val stMatrix = FloatArray(16)


        private var positionHandler = -1
        private var textureCoordHandler = -1
        private var mvpHandler = -1
        private var stHandller = -1

        private val mvpMatrix = FloatArray(16)


        constructor() {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)

            progrom = GLES20.glCreateProgram()
            if (progrom <= 0) {
                throw RuntimeException("create OpenGL Program error~")
            }

            Matrix.setIdentityM(mvpMatrix, 0)

            GLES20.glAttachShader(progrom, vertexShader)
            GLES20.glAttachShader(progrom, fragmentShader)
            GLES20.glLinkProgram(progrom)

            textureId = genTexture()

            positionHandler = GLES20.glGetAttribLocation(progrom, "aPosition")
            GLUtils.checkGlError("glGetAttribLocation")
            textureCoordHandler = GLES20.glGetAttribLocation(progrom, "aTextureCoord")
            mvpHandler = GLES20.glGetUniformLocation(progrom, "uMVPMatrix")
            stHandller = GLES20.glGetUniformLocation(progrom, "uSTMatrix")

        }

        private fun loadShader(type: Int, sourceCode: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLUtils.checkGlError("glCreateShader")
            GLES20.glShaderSource(shader, sourceCode)
            GLES20.glCompileShader(shader)

            return shader
        }

        private fun genTexture(): Int {
            val textures = IntArray(1)
            // 创建一个纹理
            GLES20.glGenTextures(1, textures, 0)
            GLUtils.checkGlError("glGenTextures")
            val textureId = textures[0]
            // 绑定到对应的纹理类型
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)

            // 设置纹理环绕方式
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )

            // 设置纹理采样过滤模式
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST.toFloat()
            )

            // 解绑纹理
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)

            return textureId
        }

        fun drawFrame() {
            GLES20.glUseProgram(progrom)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)

            GLES20.glEnableVertexAttribArray(positionHandler)
            GLUtils.checkGlError("glEnableVertexAttribArray")
            GLES20.glVertexAttribPointer(
                positionHandler, FULL_RECTANGLE_COORDS.size / 4, GLES20.GL_FLOAT, false, 8,
                FULL_RECTANGLE_BUF
            )
            GLUtils.checkGlError("glVertexAttribPointer")


            GLES20.glEnableVertexAttribArray(textureCoordHandler)
            GLUtils.checkGlError("glEnableVertexAttribArray")
            GLES20.glVertexAttribPointer(
                textureCoordHandler, FULL_RECTANGLE_TEX_COORDS.size / 4, GLES20.GL_FLOAT, false, 8,
                FULL_RECTANGLE_TEX_BUF
            )
            GLUtils.checkGlError("glVertexAttribPointer")

            GLES20.glUniformMatrix4fv(mvpHandler, 1, false, mvpMatrix, 0)
            GLUtils.checkGlError("glUniformMatrix4fv")
            GLES20.glUniformMatrix4fv(stHandller, 1, false, stMatrix, 0)
            GLUtils.checkGlError("glUniformMatrix4fv")

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            GLUtils.checkGlError("glDrawArrays")

            GLES20.glDisableVertexAttribArray(positionHandler)
            GLES20.glDisableVertexAttribArray(textureCoordHandler)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
            GLES20.glUseProgram(0)
        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        requestRender()
    }
}