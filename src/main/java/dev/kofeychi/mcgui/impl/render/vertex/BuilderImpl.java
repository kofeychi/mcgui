package dev.kofeychi.mcgui.impl.render.vertex;

import dev.kofeychi.mcgui.api.render.vertex.Builder;
import dev.kofeychi.mcgui.api.render.vertex.Built;
import dev.kofeychi.mcgui.api.render.vertex.format.Format;
import org.lwjgl.system.MemoryUtil;

public class BuilderImpl implements Builder {
    private long address; // Native memory address
    private int capacity; // Total bytes allocated
    private int pointer;  // Current write offset in bytes
    private int vertexCount;

    private final Format format;

    public BuilderImpl(int initialCapacity, Format format) {
        this.capacity = initialCapacity;
        this.address = MemoryUtil.nmemAlloc(initialCapacity);
        this.format = format;
    }

    private void ensureCapacity(int additionalBytes) {
        if (pointer + additionalBytes > capacity) {
            int newCapacity = capacity * 2;
            while (pointer + additionalBytes > newCapacity) newCapacity *= 2;
            address = MemoryUtil.nmemRealloc(address, newCapacity);
            capacity = newCapacity;
        }
    }

    @Override
    public Builder vertex(float x, float y, float z) {
        ensureCapacity(12);
        MemoryUtil.memPutFloat(address + pointer, x);
        MemoryUtil.memPutFloat(address + pointer + 4, y);
        MemoryUtil.memPutFloat(address + pointer + 8, z);
        pointer += 12;
        return this;
    }

    @Override
    public Builder color(float r, float g, float b, float a) {
        ensureCapacity(16);
        MemoryUtil.memPutFloat(address + pointer, r);
        MemoryUtil.memPutFloat(address + pointer + 4, g);
        MemoryUtil.memPutFloat(address + pointer + 8, b);
        MemoryUtil.memPutFloat(address + pointer + 12, a);
        pointer += 16;
        return this;
    }

    @Override
    public Builder texture(float u, float v) {
        ensureCapacity(8);
        MemoryUtil.memPutFloat(address + pointer, u);
        MemoryUtil.memPutFloat(address + pointer + 4, v);
        pointer += 8;
        return this;
    }

    @Override
    public Builder normal(float x, float y, float z) {
        ensureCapacity(12);
        MemoryUtil.memPutFloat(address + pointer, x);
        MemoryUtil.memPutFloat(address + pointer + 4, y);
        MemoryUtil.memPutFloat(address + pointer + 8, z);
        pointer += 12;
        return this;
    }

    @Override
    public void push() {
        vertexCount++;
        if(((long) vertexCount *format.getStride())!=pointer){
            throw new RuntimeException("Buffer contains invalid data,expected: "+(vertexCount*format.getStride())+", actual: "+(pointer));
        }
    }

    @Override
    public Built build() {
        long finishedAddress = MemoryUtil.nmemAlloc(pointer);
        MemoryUtil.memCopy(address, finishedAddress, pointer);

        Built built = Built.from(finishedAddress, pointer, vertexCount, format);

        pointer = 0;
        vertexCount = 0;
        return built;
    }

    @Override
    public void close() {
        if (address != 0) {
            MemoryUtil.nmemFree(address);
            address = 0;
        }
    }
}
