package com.course.testingdemo.book;

/*
 * A book with this ISBN already exists -> 409 Conflict.
 */
public class DuplicateIsbnException extends RuntimeException {

    public DuplicateIsbnException(String isbn) {
        super("A book with ISBN " + isbn + " already exists");
    }
}
