package org.elasticsearch.plugin.distfs.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class File implements Comparable<File> {
    private String uuid;
    private String path;
    private String contentType;
    private String content;
    private String fileName;

    public void setPath(String path) {
        this.path = path;
        this.fileName = path.substring(path.lastIndexOf("/") + 1);
    }

    @Override
    public int compareTo(File other) {
        return uuid.compareTo(other.getUuid());
    }
}
