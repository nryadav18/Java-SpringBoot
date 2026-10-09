package com.course.flywaymysql.book;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * What the API RETURNS. We never expose the entity directly (topic 33).
 */
public record BookResponse(Long id, String title, String author, BigDecimal price,
                           Integer publishedYear, String isbn, LocalDateTime createdAt) {

    static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getPrice(),
                book.getPublishedYear(), book.getIsbn(), book.getCreatedAt());
    }
}
