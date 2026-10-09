package com.course.files.storage;

/* The requested file does not exist in the upload folder. -> HTTP 404 */
public class StoredFileNotFoundException extends RuntimeException {

    public StoredFileNotFoundException(String filename) {
        super("File not found: " + filename);
    }
}
