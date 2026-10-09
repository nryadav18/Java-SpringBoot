package com.course.exceptions.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * Input DTO with validation rules (topic 34).
 * When these rules fail, Spring throws MethodArgumentNotValidException -
 * and our GlobalExceptionHandler turns it into a friendly "fieldErrors" list.
 */
public record BookRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be at most {max} characters")
        String title,

        @NotBlank(message = "Author is required")
        String author,

        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "\\d{13}", message = "ISBN must be exactly 13 digits")
        String isbn,

        @NotNull(message = "Copies is required")
        @Min(value = 0, message = "Copies cannot be negative")
        Integer copies
) {
}
