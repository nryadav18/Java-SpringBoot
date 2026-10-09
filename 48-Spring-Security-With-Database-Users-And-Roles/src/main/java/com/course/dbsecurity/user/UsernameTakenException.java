package com.course.dbsecurity.user;

/*
 * Thrown when someone tries to register a username that already exists.
 * ApiExceptionHandler turns it into HTTP 409 Conflict.
 */
public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException(String username) {
        super("Username '" + username + "' is already taken");
    }
}
