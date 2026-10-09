package com.course.shopeasy.common.exception;

/**
 * Thrown when creating something that must be unique already exists
 * (same e-mail, same product SKU, same category name).
 * Mapped to HTTP 409 Conflict by GlobalExceptionHandler.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
