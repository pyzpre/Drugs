#version 120

varying vec4 texcoord;

void main() {
    // Transform the vertex position and pass the texture coordinate to the fragment shader
    gl_Position = ftransform();
    texcoord = gl_MultiTexCoord0;
}
