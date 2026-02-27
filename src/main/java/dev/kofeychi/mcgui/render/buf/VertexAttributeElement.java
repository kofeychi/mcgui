package dev.kofeychi.mcgui.render.buf;

public record VertexAttributeElement(
        int id,
        int index,
        Type type,
        int count
) {
    public int mask() {
        return 1 << this.id;
    }

    public int size() {
        return this.type.size * this.count;
    }
}
