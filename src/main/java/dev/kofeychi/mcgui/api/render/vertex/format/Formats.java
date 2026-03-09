package dev.kofeychi.mcgui.api.render.vertex.format;

public interface Formats {
    Format POS_TEX = new Format.Builder()
            .add(Elements.POSITION)
            .add(Element.from(
                    1, VertexElementType.FLOAT,2,"uv0",false
            ))
            .build();
    Format POS_COLOR = new Format.Builder()
            .add(Elements.POSITION)
            .add(Element.from(
                    1, VertexElementType.FLOAT,4,"col",false
            ))
            .build();
}
