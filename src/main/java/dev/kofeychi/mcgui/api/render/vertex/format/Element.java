package dev.kofeychi.mcgui.api.render.vertex.format;

import dev.kofeychi.mcgui.todo.render.buf.format.VertexElement;

public interface Element {
    int getIndex();

    VertexElement getVertexElement();

    int getCount();

    String getName();

    boolean isNormalized();
}
