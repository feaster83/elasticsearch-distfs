package org.elasticsearch.plugin.distfs.model;

import lombok.Data;
import lombok.Delegate;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.TreeSet;

@Data
@NoArgsConstructor
public class Directory implements Comparable<Directory> {
    private String path;
    private String name;

    @Delegate
    private Set<File> files = new TreeSet<>();


    private Set<Directory> directories = new TreeSet<>();

    public Directory(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        return path.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object otherDir) {
        if (otherDir instanceof Directory) {
            return path.equalsIgnoreCase(((Directory)otherDir).getPath());
        }
        return false;
    }

    @Override
    public int compareTo(Directory otherDir) {
        return path.compareTo(otherDir.getPath());
    }
}
