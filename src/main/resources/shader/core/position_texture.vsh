#version 330 core

layout(location = 0) in vec3 vpos;
layout(location = 1) in vec2 vuv;

uniform mat4 transformation;

out vec2 texCoords;

void main() {
    gl_Position = transformation * vec4(vpos,1.0);
    texCoords = vuv;
}