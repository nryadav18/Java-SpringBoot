package com.course.files.storage;

/* Something is wrong with an upload (empty file, forbidden type, unsafe name, disk error). -> HTTP 400 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
