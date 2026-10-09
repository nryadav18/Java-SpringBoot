package com.course.jwtauth.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * AuthDtos - JSON bodies for /api/auth/** (records as DTOs).
 */
public final class AuthDtos {

    private AuthDtos() { }

    /** {"username":"bob","password":"bob12345"} */
    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Size(min = 8, max = 72) String password) { }

    /** {"username":"alice","password":"alice123"} */
    public record LoginRequest(@NotBlank String username, @NotBlank String password) { }

    /**
     * What the client gets back after login/register:
     * {"accessToken":"eyJ...","tokenType":"Bearer","expiresIn":900,"username":"alice","role":"USER"}
     */
    public record AuthResponse(String accessToken, String tokenType, long expiresIn, String username, String role) { }
}
