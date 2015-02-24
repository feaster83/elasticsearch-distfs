package org.elasticsearch.plugin.distfs.helper;

import org.elasticsearch.plugin.distfs.model.File;
import org.elasticsearch.plugin.distfs.model.DocumentField;

import java.util.Map;
import java.util.TreeMap;

import static org.elasticsearch.plugin.distfs.helper.PathUtils.getValidPath;

public class FileMapper {
    public static File toFile(Map<String, Object> data) {
        File file = new File();
        file.setUuid((String) data.get(DocumentField.UUID));
        file.setPath(getValidPath((String) data.get(DocumentField.PATH)));
        file.setContentType((String) data.get(DocumentField.CONTENT_TYPE));
        file.setContent((String) data.get(DocumentField.CONTENT));
        return file;
    }

    public static Map<String, Object> toDocument(File file) {
        Map<String, Object> document = new TreeMap<>();
        document.put(DocumentField.UUID, file.getUuid());
        document.put(DocumentField.PATH, getValidPath(file.getPath()));
        document.put(DocumentField.CONTENT_TYPE, file.getContentType());
        document.put(DocumentField.CONTENT, file.getContent());
        return document;
    }
}
