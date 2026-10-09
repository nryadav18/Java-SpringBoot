package com.course.testingdemo.book;

import java.math.BigDecimal;

/*
 * What the API returns for a book.
 */
public record BookResponse(Long id, String title, String author, String isbn, BigDecimal price) {

    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPrice());
    }
}
