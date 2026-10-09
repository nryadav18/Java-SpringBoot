/*
 * Where uploaded product images are stored. Two implementations:
 *   LocalDiskStorage  (dev)  - a folder on your laptop
 *   CloudStorage      (prod) - a cloud bucket (simulated)
 */
package com.course.configprofiles.storage;

public interface StorageService {

    String save(String fileName);

    String name();
}
