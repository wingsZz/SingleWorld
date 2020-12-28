package com.soul.android.single.world.alpha;

/**
 * 这里的shade来自 https://github.com/MasayukiSuda/ExoPlayerFilter
 */
public class AlphaFrameFilter extends GlFilter {
    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying highp vec2 vTextureCoord;\n" +
                    "varying highp vec2 vTextureCoord2;\n" +
                    "void main() {\n" +
                    "gl_Position = aPosition;\n" +
                    "vTextureCoord = vec2(aTextureCoord.x, aTextureCoord.y*0.5+0.5);\n" +
                    "vTextureCoord2 = vec2(aTextureCoord.x, aTextureCoord.y*0.5);\n" +
                    "}\n";
    private static final String FRAGMENT_SHADER =
            "precision highp float;\n" +
                    "varying highp vec2 vTextureCoord;\n" +
                    "varying highp vec2 vTextureCoord2;\n" +
                    "uniform highp sampler2D sTexture;\n" +
                    "void main() {\n" +
                    "vec4 color1 = texture2D(sTexture, vTextureCoord);\n" +
                    "vec4 color2 = texture2D(sTexture, vTextureCoord2);\n" +
                    "gl_FragColor = vec4(color1.rgb, color2.r);\n" +
                    "}\n";


    public AlphaFrameFilter() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }
}
