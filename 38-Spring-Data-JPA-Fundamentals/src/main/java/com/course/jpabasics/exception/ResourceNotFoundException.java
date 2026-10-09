package com.course.jpabasics.exception;

/*
 * Thrown when a product id does not exist. The GlobalExceptionHandler turns it into HTTP 404.
 * It is unchecked (extends RuntimeException) so service methods stay clean (topic 10 / 35).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with id " + id + " was not found");
    }
}
