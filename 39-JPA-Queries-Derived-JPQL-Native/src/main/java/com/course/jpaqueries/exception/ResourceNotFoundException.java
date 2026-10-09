package com.course.jpaqueries.exception;

/* Thrown when a book cannot be found. Turned into HTTP 404 by GlobalExceptionHandler. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
