package com.course.exceptions.dto;

/* Output DTO for a book. */
public record BookResponse(Long id, String title, String author, String isbn, int copiesAvailable) {
}
