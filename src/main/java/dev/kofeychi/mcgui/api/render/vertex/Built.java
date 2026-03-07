package dev.kofeychi.mcgui.api.render.vertex;

import dev.kofeychi.mcgui.api.render.vertex.format.Format;
import dev.kofeychi.mcgui.impl.render.vertex.BuiltImpl;

public interface Built extends AutoCloseable {
    static Built from(long address,int size,int vert,Format format) {
        return new BuiltImpl(
                address,size,vert,format
        );
    }

    void close();

    Mesh toMesh();

    long address();

    int sizeBytes();

    int vertexCount();

    Format format();

}
