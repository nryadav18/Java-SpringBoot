/*
 * METHOD-LEVEL @Profile: one configuration class, different @Bean methods per profile.
 *
 * Both methods register a bean with the SAME name "storageService", but only one of
 * them is active at a time. Code that needs storage just asks for a StorageService.
 *
 * @Profile("!prod") means "any profile EXCEPT prod" - handy for demo/sample data.
 */
package com.course.configprofiles.config;

import com.course.configprofiles.storage.CloudStorage;
import com.course.configprofiles.storage.LocalDiskStorage;
import com.course.configprofiles.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class StorageConfig {

    @Bean("storageService")
    @Profile("dev")
    public StorageService localStorage(@Value("${storage.root}") String root) {
        return new LocalDiskStorage(root);
    }

    @Bean("storageService")
    @Profile("prod")
    public StorageService cloudStorage(@Value("${storage.bucket}") String bucket) {
        return new CloudStorage(bucket);
    }

    /** Only when NOT in prod: load fake products so developers have something to click on. */
    @Bean
    @Profile("!prod")
    public String sampleDataLoader() {
        System.out.println("   [config] Loading 3 sample products (profile is not prod)");
        return "sample-data-loaded";
    }
}
