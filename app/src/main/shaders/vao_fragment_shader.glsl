#version 120

precision mediump float;
varying vec4 outColor;
varying vec4 outPosition;

void main() {
    float n = 10.0;
    float span = 1.0;
    int i = int((outPosition.x + 0.5)/span);
    int j = int((outPosition.y + 0.5)/span);

    int grayColor = int(mod(float(i+j), 2.0));
    if (grayColor == 1){
        float luminance = outColor.r*0.299 + outColor.g*0.587 + outColor.b*0.114;
        gl_FragColor = vec4(vec3(luminance), outColor.a);
    } else {
        gl_FragColor = outColor;
    }
}
