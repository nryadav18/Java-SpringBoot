package com.course.shopeasy.user;

import java.time.Instant;

/**
 * What the API shows about a user. Note what is MISSING: the password hash and the "enabled" flag.
 * That is the whole point of DTOs (topic 33) - the entity is our private data, the DTO is the public contract.
 */
public record UserResponse(Long id, String fullName, String email, Role role, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
