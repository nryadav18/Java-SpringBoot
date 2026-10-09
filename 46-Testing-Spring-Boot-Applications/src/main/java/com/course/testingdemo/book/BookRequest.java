package com.course.testingdemo.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/*
 * Body of POST /api/books.
 */
public record BookRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 100) String author,
        @NotBlank @Size(max = 20) String isbn,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price) {
}
