#version 330 core

layout(location = 0) in vec3 vpos;
layout(location = 1) in vec4 vcolor;

uniform mat4 transformation;

out vec4 ocolor;

void main() {
    gl_Position = transformation * vec4(vpos,1.0);
    ocolor = vcolor;
}