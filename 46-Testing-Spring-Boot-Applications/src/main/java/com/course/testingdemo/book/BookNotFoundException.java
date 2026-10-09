package com.course.testingdemo.book;

/*
 * No book with this id -> 404 (see GlobalExceptionHandler).
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book with id " + id + " not found");
    }
}
