package dev.kofeychi.mcgui.render.buf;

public enum Type {
    FLOAT(4, "float"),
    UBYTE(1, "ubyte"),
    BYTE(1, "byte"),
    USHORT(2, "ushort"),
    SHORT(2, "short"),
    UINT(4, "uint"),
    INT(4, "int");

    public final int size;
    public final String name;

    Type(int size, String name) {
        this.size = size;
        this.name = name;
    }
}