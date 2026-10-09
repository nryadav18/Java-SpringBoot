package com.course.jwtauth.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.course.jwtauth.auth.AuthDtos.AuthResponse;
import com.course.jwtauth.auth.AuthDtos.LoginRequest;
import com.course.jwtauth.auth.AuthDtos.RegisterRequest;

import jakarta.validation.Valid;

/*
 * AuthController - the two PUBLIC endpoints (permitAll for /api/auth/** in SecurityConfig).
 *
 *   curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
 *        -d '{"username":"alice","password":"alice123"}'
 *   -> {"accessToken":"eyJhbGciOiJIUzI1NiJ9...","tokenType":"Bearer","expiresIn":900,...}
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
