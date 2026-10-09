package com.course.pagination.exception;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * Bad paging/sorting input should give the client a clear 400, not a 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Our own checks: unknown sort field, negative page, size too big, wrong direction ...
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail badRequest(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Thrown by Spring Data if a sort property does not exist on the entity (safety net).
    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail unknownProperty(PropertyReferenceException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
