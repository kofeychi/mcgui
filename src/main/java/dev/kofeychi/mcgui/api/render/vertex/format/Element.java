package dev.kofeychi.mcgui.api.render.vertex.format;

import dev.kofeychi.mcgui.impl.render.vertex.format.ElementImpl;

public interface Element {
    static Element from(int index,VertexElementType elementType,int count,String name,boolean normalize) {
        return new ElementImpl(
                index,elementType,count,name,normalize
        );
    }

    int getIndex();

    VertexElementType getType();

    int getCount();

    String getName();

    boolean isNormalized();

    default int size() {
        return getType().size*getCount();
    }
}
