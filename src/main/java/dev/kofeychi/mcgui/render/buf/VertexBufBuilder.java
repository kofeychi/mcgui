package dev.kofeychi.mcgui.render.buf;

public interface VertexBufBuilder {
    VertexBufBuilder vertex(float x, float y, float z);

    VertexBufBuilder texture(float u, float v);

    VertexBufBuilder normal(float x, float y, float z);

    VertexBufBuilder color(int r, int g, int b, int a);

    VertexBufBuilder push();

    VertexBufBuilder build();
}
