package com.course.jdbctemplate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/*
 * Input DTO for creating / updating a student (topics 33 + 34).
 * Example JSON:
 * { "name":"Kiran Kumar", "email":"kiran@college.edu", "course":"Java",
 *   "marks":88, "enrolledOn":"2026-07-01" }
 */
public record StudentRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 50) String course,
        @NotNull @Min(0) @Max(100) Integer marks,
        @NotNull @PastOrPresent LocalDate enrolledOn
) {
}
