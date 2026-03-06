package dev.kofeychi.mcgui.todo.render.buf;

import dev.kofeychi.mcgui.todo.render.buf.format.VertexFormat;
import org.lwjgl.system.MemoryUtil;

public class BufferedBuilder implements AutoCloseable,BufBuilder {
    private long address; // Native memory address
    private int capacity; // Total bytes allocated
    private int pointer;  // Current write offset in bytes
    private int vertexCount;

    private final VertexFormat format;
    private final int mode;

    public BufferedBuilder(int initialCapacity, VertexFormat format, int mode) {
        this.capacity = initialCapacity;
        this.address = MemoryUtil.nmemAlloc(initialCapacity);
        this.format = format;
        this.mode = mode;
    }

    private void ensureCapacity(int additionalBytes) {
        if (pointer + additionalBytes > capacity) {
            int newCapacity = capacity * 2;
            while (pointer + additionalBytes > newCapacity) newCapacity *= 2;
            address = MemoryUtil.nmemRealloc(address, newCapacity);
            capacity = newCapacity;
        }
    }

    public BufferedBuilder vertex(float x, float y, float z) {
        ensureCapacity(12);
        MemoryUtil.memPutFloat(address + pointer, x);
        MemoryUtil.memPutFloat(address + pointer + 4, y);
        MemoryUtil.memPutFloat(address + pointer + 8, z);
        pointer += 12;
        return this;
    }

    public BufferedBuilder color(float r, float g, float b, float a) {
        ensureCapacity(16);
        MemoryUtil.memPutFloat(address + pointer, r);
        MemoryUtil.memPutFloat(address + pointer + 4, g);
        MemoryUtil.memPutFloat(address + pointer + 8, b);
        MemoryUtil.memPutFloat(address + pointer + 12, a);
        pointer += 16;
        return this;
    }

    public BufferedBuilder texture(float u, float v) {
        ensureCapacity(8);
        MemoryUtil.memPutFloat(address + pointer, u);
        MemoryUtil.memPutFloat(address + pointer + 4, v);
        pointer += 8;
        return this;
    }

    public BufferedBuilder normal(float x, float y, float z) {
        ensureCapacity(12);
        MemoryUtil.memPutFloat(address + pointer, x);
        MemoryUtil.memPutFloat(address + pointer + 4, y);
        MemoryUtil.memPutFloat(address + pointer + 8, z);
        pointer += 12;
        return this;
    }

    public BufBuilder push() {
        vertexCount++;
        if(((long) vertexCount *format.getStride())!=pointer){
            throw new RuntimeException("Buffer contains invalid data,expected: "+(vertexCount*format.getStride())+", actual: "+(pointer));
        }
        return null;
    }

    public BuiltBuffer build() {
        int size = pointer;
        long finishedAddress = MemoryUtil.nmemAlloc(size);
        MemoryUtil.memCopy(address, finishedAddress, size);

        BuiltBuffer built = new BuiltBuffer(finishedAddress, size, vertexCount, format, mode);

        // Reset builder for next use
        pointer = 0;
        vertexCount = 0;

        return built;
    }

    public void close() {
        if (address != 0) {
            MemoryUtil.nmemFree(address);
            address = 0;
        }
    }
}