package dev.kofeychi.mcgui.impl.render.vertex.format;

import dev.kofeychi.mcgui.api.render.vertex.format.Element;
import dev.kofeychi.mcgui.api.render.vertex.format.Format;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collection;
import java.util.List;

public class FormatImpl implements Format {
    private final ObjectArrayList<Element> elements;
    private final int stride;

    public FormatImpl(Collection<Element> elements) {
        this.elements = new ObjectArrayList<>(elements);
        this.stride = elements.stream().mapToInt(Element::size).sum();
    }
    @Override
    public int getStride() {
        return stride;
    }

    @Override
    public List<Element> getElements() {
        return elements;
    }
}
