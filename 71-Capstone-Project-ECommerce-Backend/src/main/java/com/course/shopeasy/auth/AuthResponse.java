package com.course.shopeasy.auth;

import com.course.shopeasy.user.UserResponse;

/**
 * Returned after a successful register or login.
 * <pre>
 * { "accessToken": "eyJhbGciOiJIUzI1NiJ9...", "tokenType": "Bearer", "expiresIn": 3600,
 *   "user": { "id": 2, "fullName": "Demo Customer", "email": "customer@shopeasy.com", "role": "CUSTOMER" } }
 * </pre>
 *
 * @param expiresIn seconds until the token expires - the front-end can log the user out (or refresh) in time.
 */
public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {

    public static AuthResponse bearer(String token, long expiresInSeconds, UserResponse user) {
        return new AuthResponse(token, "Bearer", expiresInSeconds, user);
    }
}
