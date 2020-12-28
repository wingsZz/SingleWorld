#version 120


// 试图变换矩阵
uniform mat4 uMVPMatrix;
// 纹理变换矩阵
uniform mat4 uSTMatrix;
// 视图坐标
attribute vec4 aPosition;
// 纹理坐标
attribute vec4 aTextureCoord;
// 变换之后输出的纹理坐标
varying vec2 vTextureCoord;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    // 因为这里使用的2D纹理，最终使用到的坐标是一个二维坐标
    vTextureCoord = (uSTMatrix * aTextureCoord).xy;
}
