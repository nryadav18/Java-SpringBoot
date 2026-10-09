package com.course.composeapp.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * BookRequest.java - the JSON the client sends to create a book (a DTO, see topic 33).
 *
 *   { "title": "Clean Code", "author": "Robert C. Martin" }
 */
public record BookRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 100) String author) {
}
