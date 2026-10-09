package com.course.jdbctemplate.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/*
 * Global error handling (topic 35) using ProblemDetail.
 * Extending ResponseEntityExceptionHandler also gives us clean 400 responses
 * for validation errors, bad JSON, wrong types, unknown URLs, ...
 *
 * Interesting for THIS topic: Spring translates database errors into its own
 * DataAccessException hierarchy - the SAME exception classes for H2, MySQL,
 * PostgreSQL, Oracle... For example a UNIQUE constraint violation becomes
 * DuplicateKeyException, whatever database you use.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleNotFound(StudentNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** The email column is UNIQUE. Inserting the same email twice -> DuplicateKeyException -> 409. */
    @ExceptionHandler(DuplicateKeyException.class)
    public ProblemDetail handleDuplicate(DuplicateKeyException ex) {
        // Do NOT return ex.getMessage(): it contains the SQL statement and table details.
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "A student with this email already exists");
    }

    /** Any other database problem: log it, hide details from the client. */
    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDatabase(DataAccessException ex) {
        logger.error("Database error", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "A database error occurred. Please try again later.");
    }
}
