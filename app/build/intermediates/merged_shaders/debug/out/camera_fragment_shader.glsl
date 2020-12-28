#version 120

precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES texture;

void main() {
    gl_FragColor = texture2D(texture, vTextureCoord);
}
