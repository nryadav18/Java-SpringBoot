package com.course.flywaymysql.book;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Thrown when a book id does not exist. @ResponseStatus turns it into a 404
 * (we saw this in topic 35).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book with id " + id + " not found");
    }
}
