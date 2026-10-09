package com.course.flywaymysql.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/*
 * What the client SENDS to create a book (a DTO - topic 33).
 * Validation (topic 34) stops bad data before it reaches the database.
 * The @Size limits match the VARCHAR sizes in the Flyway scripts.
 */
public record BookRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 100) String author,
        @NotNull @Positive BigDecimal price,
        Integer publishedYear,
        @Size(max = 20) String isbn) {
}
