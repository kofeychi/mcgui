package dev.kofeychi.mcgui.api.render.vertex.format;


public interface Elements {
    Element POSITION = Element.from(
            0, VertexElementType.FLOAT,3,"position",false
    );
    Element COLOR = Element.from(
            1, VertexElementType.FLOAT,4,"color",false
    );
    Element UV0 = Element.from(
            2, VertexElementType.FLOAT,2,"uv0",false
    );
}
