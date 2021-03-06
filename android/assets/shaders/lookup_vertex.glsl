attribute vec4 a_position;
attribute vec4 a_texCoord0;

varying vec2 v_texCoords;
uniform mat4 u_projTrans;

void main() {
    v_texCoords = a_texCoord0.xy;
    gl_Position = u_projTrans * a_position;
}