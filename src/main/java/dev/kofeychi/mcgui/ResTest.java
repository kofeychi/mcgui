package dev.kofeychi.mcgui;

import dev.kofeychi.mcgui.resource.Id;
import dev.kofeychi.mcgui.resource.PackType;
import dev.kofeychi.mcgui.resource.types.InjarPackSource;

public class ResTest {
    public static PackType builtin_client = new PackType(
            "client",
            Id.of("builtin","client")
    );
    public static void main(String[] args) throws Exception {
        var ps = new InjarPackSource(
                ResTest.class,
                Id.of("builtin","test"),
                builtin_client
        );
        System.out.println("flat");
        ps.list(p->!p.isDir()).forEach(System.out::println);
        System.out.println("recursive");
        ps.listRecursive(p->!p.isDir()).forEach(System.out::println);
        /*
        System.out.println(sosi.target());
        var t = System.nanoTime();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(sosi.read()))) {
            var diff = System.nanoTime() - t;
            System.out.println(diff);
            br.lines().forEach(System.out::println);
        }
         */
    }
}
