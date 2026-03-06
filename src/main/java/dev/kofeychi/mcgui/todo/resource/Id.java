package dev.kofeychi.mcgui.todo.resource;

public class Id {
    public final String namespace;
    public final String path;

    private Id(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }
    public static Id of(String namespace, String path) {
        return new Id(namespace, path);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    public Id appendPath(String path) {
        if(path.endsWith("/")) {
            path = path.substring(0, path.length()-1);
        }
        return new Id(namespace, path + this.path);
    }

    public Id resolvePath(String path) {
        var outpath = this.path;
        if(!this.path.endsWith("/")&&!path.startsWith("/")) {
            outpath += "/";
        }
        return new Id(namespace, outpath + path);
    }

    public String asOsPath() {
        return namespace.isEmpty()&&path.isEmpty() ? "" : namespace + "/" + path;
    }
}
