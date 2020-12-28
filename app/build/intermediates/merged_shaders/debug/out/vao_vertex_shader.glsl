#version 120

attribute vec4 aPosition;
attribute vec4 aColor;
varying vec4 outPosition;
varying vec4 outColor;

void main() {
    gl_Position = aPosition;
    outPosition = gl_Position;
    outColor = aColor;
}
