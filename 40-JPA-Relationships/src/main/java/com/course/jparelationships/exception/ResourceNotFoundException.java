package com.course.jparelationships.exception;

/* Thrown when a customer / order / product id does not exist -> HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
