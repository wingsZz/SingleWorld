#version 120 es

precision mediump float;
varying vec2 v_texCoord;
uniform sampler2D y_texture;
uniform sampler2D uv_texture;

void main() {
    vec3 yuv;
    yuv.x = texture(y_texture, v_texCoord).r;
    yuv.y = texture(uv_texture, v_texCoord).a - 0.5;
    yuv.z = texture(uv_texture, v_texCoord).r - 0.5;

    vec3 rgb = mat3(1.0, 1.0, 1.0,
    0.0, -0.344, 1.770,
    1.403, -0.714, 0.0) * yuv;
    gl_FragColor = vec4(rgb, 1);
}
