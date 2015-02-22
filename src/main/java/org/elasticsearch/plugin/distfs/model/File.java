package org.elasticsearch.plugin.distfs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class File implements Comparable<File> {
    private String uuid;
    private String path;
    private String contentType;
    private String content;

    public String getFilename() {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    @Override
    public int compareTo(File other) {
        return uuid.compareTo(other.getUuid());
    }
}
