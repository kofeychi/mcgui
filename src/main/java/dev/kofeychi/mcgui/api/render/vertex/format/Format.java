package dev.kofeychi.mcgui.api.render.vertex.format;

import java.util.List;

public interface Format {
    int getStride();

    List<Element> getElements();
}
