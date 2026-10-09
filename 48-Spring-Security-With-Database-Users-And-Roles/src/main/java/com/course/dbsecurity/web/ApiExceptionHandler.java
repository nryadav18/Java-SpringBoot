package com.course.dbsecurity.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.course.dbsecurity.user.UsernameTakenException;

/*
 * ApiExceptionHandler - turns our business exceptions into clean JSON (ProblemDetail, topic 35).
 *
 * WARNING: do NOT add a catch-all @ExceptionHandler(Exception.class) here!
 * It would also catch Spring Security's AccessDeniedException thrown by @PreAuthorize
 * and turn a correct 403 into a wrong 500. Let security exceptions pass through.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UsernameTakenException.class)
    public ProblemDetail usernameTaken(UsernameTakenException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());   // 409
    }
}
