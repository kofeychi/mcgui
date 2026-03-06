package dev.kofeychi.mcgui.todo.render.buf.format;

public record VertexElement(int index, VertexElementType type, int count, String name, boolean normalized) {
    public int getSize() {
        return type.size * count;
    }
}