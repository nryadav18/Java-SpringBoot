package com.course.jwtauth.auth;

/** Thrown on registration when the username exists already -> 409 Conflict. */
public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException(String username) {
        super("Username '" + username + "' is already taken");
    }
}
