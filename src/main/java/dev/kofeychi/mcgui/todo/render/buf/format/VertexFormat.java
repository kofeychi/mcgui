package dev.kofeychi.mcgui.todo.render.buf.format;

import java.util.ArrayList;
import java.util.List;

public class VertexFormat {
    public static final VertexFormat POSITION_COLOR_TEX = new VertexFormat.Builder()
            .add(0,VertexElementType.FLOAT,3,"pos",false)
            .add(1,VertexElementType.FLOAT,4,"col",false)
            .add(2,VertexElementType.FLOAT,2,"uv",false)
            .build();
    private final List<VertexElement> elements;
    private final int stride;

    private VertexFormat(List<VertexElement> elements) {
        this.elements = elements;
        this.stride = elements.stream().mapToInt(VertexElement::getSize).sum();
    }

    public int getStride() { return stride; }

    public List<VertexElement> getElements() { return elements; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final List<VertexElement> elements = new ArrayList<>();

        public Builder add(int index, VertexElementType type, int count, String name, boolean normalized) {
            elements.add(new VertexElement(index, type, count, name, normalized));
            return this;
        }

        public VertexFormat build() {
            return new VertexFormat(List.copyOf(elements));
        }
    }
}