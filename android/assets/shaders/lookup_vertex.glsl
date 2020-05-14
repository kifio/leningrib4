attribute lowp vec4 a_position;

attribute lowp vec4 a_texCoord0;
varying lowp vec2 v_texCoords;
uniform highp mat4 u_projTrans;


void main() {
    v_texCoords = a_texCoord0.xy;
    gl_Position = u_projTrans * a_position;
}