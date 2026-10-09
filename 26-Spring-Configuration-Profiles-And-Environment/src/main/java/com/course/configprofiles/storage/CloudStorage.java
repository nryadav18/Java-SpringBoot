/*
 * Prod implementation: pretends to upload files to a cloud bucket (like Amazon S3).
 * Plain class - registered by StorageConfig with @Bean + @Profile("prod").
 */
package com.course.configprofiles.storage;

public class CloudStorage implements StorageService {

    private final String bucket;

    public CloudStorage(String bucket) {
        this.bucket = bucket;
    }

    @Override
    public String save(String fileName) {
        return "uploaded to cloud bucket '" + bucket + "' as " + fileName;
    }

    @Override
    public String name() {
        return "Cloud";
    }
}
