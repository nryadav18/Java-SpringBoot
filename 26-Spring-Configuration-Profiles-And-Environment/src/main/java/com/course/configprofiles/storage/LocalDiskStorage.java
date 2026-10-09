/*
 * Dev implementation: pretends to save files into a local folder.
 * Plain class - registered by StorageConfig with @Bean + @Profile("dev").
 */
package com.course.configprofiles.storage;

public class LocalDiskStorage implements StorageService {

    private final String rootFolder;

    public LocalDiskStorage(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    @Override
    public String save(String fileName) {
        return "saved to " + rootFolder + "/" + fileName;
    }

    @Override
    public String name() {
        return "LocalDisk";
    }
}
