package org.elasticsearch.plugin.distfs.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.plugin.distfs.model.Directory;
import org.elasticsearch.plugin.distfs.model.File;

import java.util.Collection;

import static org.elasticsearch.plugin.distfs.helper.PathUtils.getFirstRelativeDirName;
import static org.elasticsearch.plugin.distfs.helper.PathUtils.getRelativePath;
import static org.elasticsearch.plugin.distfs.helper.PathUtils.isFileInDir;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class DirectoryMapper {

    public static Directory toDirectory(String directoryPath, String esPath, Collection<File> files) {
        log.debug("DirectoryMapper.toDirectory({}, {}, files)", directoryPath, esPath);

        Directory directory = new Directory(directoryPath, esPath);
        directory.setName(getFirstRelativeDirName(directoryPath));
        log.debug("Creating directory: {}", directoryPath);

        files.forEach(file -> {
            String relativePath = getRelativePath(directory, file);
            if (isFileInDir(directory, file)) {
                log.debug("Adding file: {} to directory.", file.getFileName());
                directory.add(file);
            } else { // Not a subdirectory
                log.debug("Adding subdir: {} to directory.", relativePath);
                String subDirName = getFirstRelativeDirName(relativePath);
                String subDirPath = directoryPath + (isRootPath(directoryPath) ? "" : "/") + subDirName;
                String subDirESPath = directory.getEsPath() + "/" + subDirName;
                Directory subDir = new Directory(subDirPath, subDirESPath);
                subDir.setName(subDirName);
                directory.add(subDir);

            }
        });
        return directory;
    }

    private static boolean isRootPath(String directoryPath) {
        return directoryPath.equalsIgnoreCase("/");
    }

}
