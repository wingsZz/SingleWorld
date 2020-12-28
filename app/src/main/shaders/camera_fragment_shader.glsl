#version 120

precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES texture;

void main() {
    gl_FragColor = texture2D(texture, vTextureCoord);
}
void main() {
    vec4 color = texture2D(sTexture, vTextureCoord);
    if (noFilter == 1){
        gl_FragColor = textureColor;
    } else {
        gl_FragColor = lookup(color, lookupTexture);
    }
}