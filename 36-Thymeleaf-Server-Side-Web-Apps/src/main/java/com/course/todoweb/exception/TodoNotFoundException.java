package com.course.todoweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Thrown when a todo id does not exist.
 * For a BROWSER request, Spring Boot then renders templates/error/404.html
 * (a friendly HTML page instead of JSON).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("Todo " + id + " not found");
    }
}
