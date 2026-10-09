package com.course.jwtauth.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.course.jwtauth.auth.UsernameTakenException;

/*
 * ApiExceptionHandler - clean JSON errors for the auth endpoints.
 *
 * AuthenticationException covers BadCredentialsException (wrong password / unknown user),
 * DisabledException, LockedException ... -> 401.
 * The message is deliberately vague: never tell an attacker WHICH part was wrong.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail badLogin(AuthenticationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    @ExceptionHandler(UsernameTakenException.class)
    public ProblemDetail usernameTaken(UsernameTakenException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
}
