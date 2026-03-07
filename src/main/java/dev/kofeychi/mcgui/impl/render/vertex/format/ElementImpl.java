package dev.kofeychi.mcgui.impl.render.vertex.format;

import dev.kofeychi.mcgui.api.render.vertex.format.Element;
import dev.kofeychi.mcgui.todo.render.buf.format.VertexElementType;

public class ElementImpl implements Element {
    private final int index;
    private final VertexElementType type;
    private final int count;
    private final String name;
    private final boolean isNormalized;

    public ElementImpl(int index, VertexElementType type, int count, String name, boolean isNormalized) {
        this.index = index;
        this.type = type;
        this.count = count;
        this.name = name;
        this.isNormalized = isNormalized;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public VertexElementType getType() {
        return type;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isNormalized() {
        return isNormalized;
    }
}
