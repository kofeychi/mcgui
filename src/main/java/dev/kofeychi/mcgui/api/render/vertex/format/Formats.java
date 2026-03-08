package dev.kofeychi.mcgui.api.render.vertex.format;

public interface Formats {
    Format POS_TEX = new Format.Builder()
            .add(Elements.POSITION)
            .add(Elements.UV0)
            .build();
    Format POS_COLOR = new Format.Builder()
            .add(Elements.POSITION)
            .add(Elements.COLOR)
            .build();
}
