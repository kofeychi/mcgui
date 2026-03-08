package dev.kofeychi.mcgui.impl.render.vertex;

import dev.kofeychi.mcgui.api.render.vertex.Built;
import dev.kofeychi.mcgui.api.render.vertex.Mesh;
import dev.kofeychi.mcgui.api.render.vertex.format.Format;
import org.lwjgl.system.MemoryUtil;

public class BuiltImpl implements Built {
    private final long adress;
    private final int size;
    private final int vertexCount;
    private final Format format;
    private boolean closed = false;

    public BuiltImpl(long adress, int size, int vertexCount, Format format) {
        this.adress = adress;
        this.size = size;
        this.vertexCount = vertexCount;
        this.format = format;
    }

    @Override
    public void close() {
        if(!closed) {
            MemoryUtil.nmemFree(adress);
            closed = true;
        } else {
            throw new IllegalStateException("Tried to close already closed");
        }
    }

    @Override
    public Mesh toMesh() {
        return Mesh.from(this);
    }

    @Override
    public long address() {
        return adress;
    }

    @Override
    public int sizeBytes() {
        return size;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public Format format() {
        return format;
    }
}
