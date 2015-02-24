package org.elasticsearch.plugin.distfs.helper;

import lombok.experimental.UtilityClass;
import org.elasticsearch.plugin.distfs.model.Directory;
import org.elasticsearch.plugin.distfs.model.File;

@UtilityClass
public class PathUtils {

    public static String getValidPath(String path) {
        String filePath = path;
        if (path.equalsIgnoreCase("/")) {
            filePath = "";
        } else if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        return filePath;
    }

    public static String getRelativePath(Directory directory, File file) {
        String relativePath = file.getPath();
        if (directory.getPath().length() > 0) {
            relativePath = file.getPath().substring(directory.getPath().length());
            relativePath = getValidPath(relativePath);
        }
        return relativePath;
    }

    public static String getFirstRelativeDirName(String relativePath) {
        return relativePath.substring(0, relativePath.indexOf("/", 1));
    }

    public static boolean isFileInDir(Directory directory, File file) {
        return !(getRelativePath(directory, file).indexOf("/", 1) > 0);
    }
}
