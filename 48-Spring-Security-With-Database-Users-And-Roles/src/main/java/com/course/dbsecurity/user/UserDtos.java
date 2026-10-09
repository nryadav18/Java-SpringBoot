package com.course.dbsecurity.user;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * UserDtos - the JSON shapes going IN and OUT of the user endpoints (records = small DTOs).
 *
 * IMPORTANT: UserResponse has NO password field. Never send a password (or its hash)
 * back to the client.
 */
public final class UserDtos {

    private UserDtos() { }

    /** Body of POST /api/auth/register  -> {"username":"bob","password":"bob12345"} */
    public record RegisterRequest(
            @NotBlank
            @Size(min = 3, max = 50)
            @Pattern(regexp = "[a-zA-Z0-9_]+", message = "only letters, digits and _")
            String username,

            @NotBlank
            @Size(min = 8, max = 72, message = "must be 8 to 72 characters") // BCrypt uses max 72 bytes
            String password) { }

    /** What we return about a user. */
    public record UserResponse(Long id, String username, boolean enabled, Set<Role> roles) {

        public static UserResponse from(AppUser user) {
            return new UserResponse(user.getId(), user.getUsername(), user.isEnabled(), Set.copyOf(user.getRoles()));
        }
    }
}
