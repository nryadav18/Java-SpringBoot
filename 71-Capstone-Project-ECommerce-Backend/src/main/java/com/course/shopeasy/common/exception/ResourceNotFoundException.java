package com.course.shopeasy.common.exception;

/**
 * Thrown when something the client asked for does not exist (or the client may not see it).
 * GlobalExceptionHandler turns it into HTTP 404 Not Found (topic 35).
 *
 * It is unchecked (extends RuntimeException) so it does not clutter every method signature,
 * and so @Transactional rolls back automatically when it is thrown (topic 42).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super("%s with id %s was not found".formatted(resource, id));
    }
}
