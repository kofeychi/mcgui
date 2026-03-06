#version 460 core
in vec4 vertc;
out vec4 FragColor;
void main() {
    if(vertc.a <= 0.1) {
        discard;
    }
    FragColor = vertc;
}