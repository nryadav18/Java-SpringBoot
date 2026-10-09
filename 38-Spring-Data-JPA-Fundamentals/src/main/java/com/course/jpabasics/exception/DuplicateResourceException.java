package com.course.jpabasics.exception;

/*
 * Thrown when a client tries to create a product with a SKU that already exists.
 * The GlobalExceptionHandler turns it into HTTP 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
