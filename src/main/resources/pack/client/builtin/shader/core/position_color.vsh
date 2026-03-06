#version 460 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;

out vec4 vertexColor;

void main() {
    gl_Position = aPos;
    vertexColor = aColor;
}