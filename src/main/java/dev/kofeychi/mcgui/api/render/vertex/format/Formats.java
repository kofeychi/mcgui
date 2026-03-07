package dev.kofeychi.mcgui.api.render.vertex.format;

public interface Formats {
    Format POS_COLOR_TEX = new Format.Builder()
            .add(Elements.POSITION)
            .add(Elements.COLOR)
            .add(Elements.UV0)
            .build();
}
