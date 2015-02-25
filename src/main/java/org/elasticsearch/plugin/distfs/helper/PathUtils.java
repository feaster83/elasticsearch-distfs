package org.elasticsearch.plugin.distfs.helper;

import org.elasticsearch.plugin.distfs.DistFSPlugin;
import org.elasticsearch.plugin.distfs.model.Directory;
import org.elasticsearch.plugin.distfs.model.File;

public class PathUtils {

    public static String getValidPath(String path) {
        String filePath;
        if (path.equalsIgnoreCase("/")) {
            filePath = "";
        } else if (path.endsWith("/") && path.length() > 1) {
            filePath = path.substring(0, path.length() - 1);
        } else {
            filePath = path;
        }
        return filePath;
    }

    public static String getValidESPath(String path, String index, String type) {
        return "/" + DistFSPlugin.PLUGIN_PATH + "/" + index + "/" + type + getValidPath(path);
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
        String dirName = relativePath;
        if (dirName.indexOf("/", 1) > 0) {
            dirName = relativePath = relativePath.substring(1, relativePath.indexOf("/", 1));
        }
        return dirName;
    }

    public static boolean isFileInDir(Directory directory, File file) {
        return !(getRelativePath(directory, file).indexOf("/", 1) > 0);
    }
}
