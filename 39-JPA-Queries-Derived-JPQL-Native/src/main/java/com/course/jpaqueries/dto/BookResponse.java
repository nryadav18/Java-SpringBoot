package com.course.jpaqueries.dto;

import com.course.jpaqueries.entity.Book;
import com.course.jpaqueries.entity.Genre;

import java.math.BigDecimal;

/* The JSON shape of a full book returned by the API (never return the entity itself - topic 33). */
public record BookResponse(Long id, String title, String author, String isbn, Genre genre,
                           BigDecimal price, int publishedYear, int pages, boolean available) {

    public static BookResponse from(Book b) {
        return new BookResponse(b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getGenre(),
                b.getPrice(), b.getPublishedYear(), b.getPages(), b.isAvailable());
    }
}
