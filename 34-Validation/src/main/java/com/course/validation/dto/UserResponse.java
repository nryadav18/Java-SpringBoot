package com.course.validation.dto;

/*
 * What we send back after a successful registration.
 * Notice: NO password here - never echo secrets back (topic 33: DTOs protect data).
 */
public record UserResponse(Long id, String name, String email, String city) {
}
