package dev.kofeychi.mcgui.todo.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
@Deprecated
public interface PackSource {
    Id id();

    Id path();

    PackType type();

    String target();

    PackSource resolve(String path);

    boolean isDir();

    List<PackSource> list();

    List<PackSource> listRecursive();

    List<PackSource> list(Predicate<PackSource> filter);

    List<PackSource> listRecursive(Predicate<PackSource> filter);

    InputStream read() throws IOException;

    String readString() throws IOException;
}
