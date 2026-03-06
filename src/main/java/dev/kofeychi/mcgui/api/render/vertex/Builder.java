package dev.kofeychi.mcgui.api.render.vertex;

import dev.kofeychi.mcgui.todo.render.buf.BufBuilder;
import dev.kofeychi.mcgui.todo.render.buf.BuiltBuffer;

public interface Builder extends AutoCloseable {
    BufBuilder vertex(float x, float y, float z);

    BufBuilder texture(float u, float v);

    BufBuilder normal(float x, float y, float z);

    BufBuilder color(float r, float g, float b, float a);

    void push();

    BuiltBuffer build();

    void close();
}
