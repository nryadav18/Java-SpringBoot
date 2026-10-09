package com.course.jdbctemplate.exception;

/* Thrown by the service when no student has the given id (mapped to 404 in ApiExceptionHandler). */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(long id) {
        super("Student not found with id: " + id);
    }
}
