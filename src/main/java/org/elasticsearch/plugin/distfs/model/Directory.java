package org.elasticsearch.plugin.distfs.model;

import lombok.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Data
@NoArgsConstructor
public class Directory implements Comparable<Directory> {
    private String esPath;
    private String path;
    private String name;

    @Getter(AccessLevel.PRIVATE)
    @Delegate(types = SimpleFileCollection.class)
    private final Set<File> files = new TreeSet<>();

    @Getter(AccessLevel.PRIVATE)
    @Delegate(types = SimpleDirectoryCollection.class)
    private final Set<Directory> directories = new TreeSet<>();

    public Directory(String path, String esPath) {
        this.path = path;
        this.esPath = esPath;
    }

    public Collection<Directory> getSubDirectories() {
        return Collections.unmodifiableCollection(directories);
    }

    public Collection<File> getFiles() {
        return Collections.unmodifiableCollection(files);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object otherDir) {
        if (otherDir instanceof Directory) {
            return name.equalsIgnoreCase(((Directory)otherDir).getName());
        }
        return false;
    }

    public int compareTo(Directory otherDir) {
        return path.compareTo(otherDir.getPath());
    }

    private interface SimpleFileCollection extends SimpleCollection<File> {}
    private interface SimpleDirectoryCollection extends SimpleCollection<Directory> {}
    private interface SimpleCollection<T> {
        boolean add(T item);
        boolean remove(T item);
    }
}
