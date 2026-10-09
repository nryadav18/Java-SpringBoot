package com.course.layered.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Thrown by the SERVICE when an employee id does not exist.
 *
 * @ResponseStatus(NOT_FOUND) tells Spring: "if this exception escapes a
 * controller, answer with HTTP 404". The service code itself still knows
 * nothing about HTTP - it just throws a meaningful exception.
 * (Topic 35 shows a better, central way: @RestControllerAdvice.)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employee with id " + id + " was not found");
    }
}
