package com.course.jwtauth.user;

/*
 * Role - one role per user in this project (kept simple).
 * Becomes the authority "ROLE_USER" / "ROLE_ADMIN" for hasRole(...) checks.
 */
public enum Role {
    USER,
    ADMIN
}
