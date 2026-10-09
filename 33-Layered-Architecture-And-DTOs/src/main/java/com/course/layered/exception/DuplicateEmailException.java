package com.course.layered.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Thrown by the SERVICE when a business rule is broken:
 * "two employees cannot share the same email"  ->  HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("An employee with email " + email + " already exists");
    }
}
