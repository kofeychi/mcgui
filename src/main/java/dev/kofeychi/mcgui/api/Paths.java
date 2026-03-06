package dev.kofeychi.mcgui.api;

import java.nio.file.Path;
import java.util.Arrays;

public interface Paths {
    Path ROOT = Path.of(".");

    static void prepare(){
        Arrays.stream(Paths.class.getFields()).filter(f -> f.getType() == Path.class&&!f.getName().equals("GAMEDIR")).forEach(f -> {
            f.setAccessible(true);
            try {
                Path p = (Path) f.get(null);
                p.toFile().mkdirs();
            } catch (Exception ignored) {
            }
        });
    }

    static Resolver resolvable(Path root){
        return (s) -> root.resolve(s).toAbsolutePath();
    }

    interface Resolver{
        Path resolve(String s);
    }
}
