package com.course.restbasics;

/*
 * A Book - our REST "resource".
 *
 * Jackson (the JSON library inside spring-boot-starter-web) converts it automatically:
 *
 *   Java:  new Book(1L, "Clean Code", "Robert C. Martin", 2008, 499.0)
 *   JSON:  {"id":1,"title":"Clean Code","author":"Robert C. Martin","year":2008,"price":499.0}
 *
 * Records work perfectly with Jackson: the component names become the JSON field names.
 */
public record Book(Long id, String title, String author, int year, double price) {

    /** Returns a copy of this book with the given id (records are immutable, so we copy). */
    public Book withId(Long newId) {
        return new Book(newId, title, author, year, price);
    }
}
