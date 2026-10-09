package com.course.graphql.library;

/* Thrown when a mutation refers to a book or author id that does not exist. */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }
}
