package dev.kofeychi.mcgui.todo.resource.types;

import dev.kofeychi.mcgui.todo.resource.Id;
import dev.kofeychi.mcgui.todo.resource.PackSource;
import dev.kofeychi.mcgui.todo.resource.PackType;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

@Deprecated
public class InjarPackSource implements PackSource {
    public final JarFile jf;
    public final File jfFile;
    public final Id id;
    public final Id path;
    public final PackType type;
    public final boolean isDir;

    public InjarPackSource(Class<?> at,Id id,PackType type) {
        this(new File(at.getProtectionDomain().getCodeSource().getLocation().getFile()),id,Id.of("",""),type,false);
    }
    public InjarPackSource(File at,Id id,PackType type) {
        this(at,id,Id.of("",""),type,false);
    }
    public InjarPackSource(File at,Id id,Id path,PackType type,boolean isDir) {
        this.jfFile = at;
        this.id = id;
        this.path = path;
        this.type = type;
        this.isDir = isDir;
        try {
            this.jf = new JarFile(jfFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Id id() {
        return id;
    }

    @Override
    public Id path() {
        return path;
    }

    @Override
    public PackType type() {
        return type;
    }

    @Override
    public InputStream read() throws IOException {
        if(isDir) {
            return null;
        }
        return new FastBufferedInputStream(jf.getInputStream(new ZipEntry(target())));
    }

    @Override
    public String readString() throws IOException {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(this.read()))) {
            return br.lines().reduce((a,b)->a+"\n"+b).orElse("");
        }
    }

    @Override
    public String target() {
        return "pack" + "/" + type.folder + (type.folder.endsWith("/") ? "/" : "") + path.asOsPath();
    }

    @Override
    public PackSource resolve(String path) {
        return new InjarPackSource(jfFile, id, Id.of(this.path.namespace,path), type,!path.contains("."));
    }

    @Override
    public boolean isDir() {
        return isDir;
    }

    private List<PackSource> list(BiPredicate<Integer,Integer> comparator, Predicate<PackSource> filter) {
        var out = new ObjectArrayList<PackSource>();
        var prefix = target();
        var entries = jf.entries();
        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();
            var name = entry.getName();
            var s = new InjarPackSource(jfFile, id, Id.of(path.namespace,entry.getName()), type, !entry.isDirectory());
            if (
                    (name.startsWith(prefix) && !name.equals(prefix))
                    && (comparator.test(getDepth(name),getDepth(prefix)+1))
                    && (!filter.test(s))
            ) {
                out.add(s);
            }
        }
        return out;
    }

    @Override
    public List<PackSource> list() {
        return list(Objects::equals,p->true);
    }

    @Override
    public List<PackSource> listRecursive() {
        return list((i1,i2)->i1>=i2,p->true);
    }

    @Override
    public List<PackSource> list(Predicate<PackSource> filter) {
        return list(Objects::equals,filter);
    }

    @Override
    public List<PackSource> listRecursive(Predicate<PackSource> filter) {
        return list((i1,i2)->i1>=i2,filter);
    }

    private static int getDepth(String path) {
        if (path.isEmpty() || path.equals("/")) return 0;
        String cleanPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        int depth = 0;
        for (int i = 0; i < cleanPath.length(); i++) {
            if (cleanPath.charAt(i) == '/') {
                depth++;
            }
        }
        return depth;
    }

    @Override
    public String toString() {
        return path.asOsPath();
    }
}
