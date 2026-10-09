package com.course.exceptions.model;

/*
 * Book - the domain object stored by the repository.
 * A small mutable class: the number of available copies changes when a book is borrowed.
 */
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private int copiesAvailable;

    public Book(Long id, String title, String author, String isbn, int copiesAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.copiesAvailable = copiesAvailable;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }

    public int getCopiesAvailable() { return copiesAvailable; }
    public void setCopiesAvailable(int copiesAvailable) { this.copiesAvailable = copiesAvailable; }
}
