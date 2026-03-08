#version 330 core

in vec4 ocolor;

out vec4 fragColor;

void main() {
    if (ocolor.a == 0.0) {
        discard;
    }
    fragColor = ocolor;
}
