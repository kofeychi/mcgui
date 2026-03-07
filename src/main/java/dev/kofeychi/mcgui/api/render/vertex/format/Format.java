package dev.kofeychi.mcgui.api.render.vertex.format;

import dev.kofeychi.mcgui.impl.render.vertex.format.FormatImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Format {
    static Format from(Collection<Element> elements) {
        return new FormatImpl(elements);
    }

    int getStride();

    List<Element> getElements();

    class Builder {
        private final ArrayList<Element> elements = new ArrayList<>();

        public Builder add(Element element) {
            elements.add(element);
            return this;
        }

        public Format build() {
            return from(elements);
        }
    }
}
