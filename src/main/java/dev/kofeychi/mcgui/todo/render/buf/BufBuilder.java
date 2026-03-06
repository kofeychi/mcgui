package dev.kofeychi.mcgui.todo.render.buf;

public interface BufBuilder {
    BufBuilder vertex(float x, float y, float z);

    BufBuilder texture(float u, float v);

    BufBuilder normal(float x, float y, float z);

    BufBuilder color(float r, float g, float b, float a);

    BufBuilder push();

    BuiltBuffer build();

    void close();
}
