#version 460 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aUv;

out vec4 vertexColor;
out vec2 vertexUv;
uniform float uTime;

void main() {
    gl_Position = aPos;
    vertexColor = aColor;
    vertexUv = aUv;
}