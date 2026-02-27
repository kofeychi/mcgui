package dev.kofeychi.mcgui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

public interface PackSource {
    Id id();

    Id path();

    PackType type();

    String target();

    PackSource resolve(Id path);

    boolean isDir();

    List<PackSource> list();

    List<PackSource> listRecursive();

    List<PackSource> list(Predicate<PackSource> filter);

    List<PackSource> listRecursive(Predicate<PackSource> filter);

    InputStream read() throws IOException;
}
