package com.course.taskapi.task;

import java.util.Set;

/*
 * Thrown when the client asks to sort by a field we do not allow (?sort=colour).
 * GlobalExceptionHandler turns it into a 400 ProblemDetail.
 */
public class InvalidSortPropertyException extends RuntimeException {

    private final String property;
    private final Set<String> allowed;

    public InvalidSortPropertyException(String property, Set<String> allowed) {
        super("Cannot sort by '" + property + "'. Allowed: " + String.join(", ", allowed));
        this.property = property;
        this.allowed = allowed;
    }

    public String getProperty() { return property; }
    public Set<String> getAllowed() { return allowed; }
}
