package com.course.dbsecurity.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.course.dbsecurity.user.UserDtos.RegisterRequest;
import com.course.dbsecurity.user.UserDtos.UserResponse;
import com.course.dbsecurity.user.UserService;

import jakarta.validation.Valid;

/*
 * AuthController - public registration endpoint (permitAll in SecurityConfig).
 *
 *   curl -i -X POST http://localhost:8080/api/auth/register \
 *        -H "Content-Type: application/json" \
 *        -d '{"username":"bob","password":"bob12345"}'
 *   -> 201 {"id":3,"username":"bob","enabled":true,"roles":["USER"]}
 *
 * There is no /login endpoint: with HTTP Basic, the client simply sends
 * username + password on every request (curl -u bob:bob12345 ...).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }
}
