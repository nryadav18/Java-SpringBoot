package com.course.jpaqueries.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/*
 * Book - the entity we search in this topic.
 * ------------------------------------------
 * To keep the focus on QUERIES, the author is a simple text column here.
 * In topic 40 you will learn to turn it into a real Author entity with a relationship.
 *
 * Remember the important rule for this topic:
 *   - derived queries and JPQL use the JAVA names  (Book, publishedYear)
 *   - native SQL uses the DATABASE names          (books, published_year)
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    // EnumType.STRING stores "FANTASY". Never use the default ORDINAL (stores 0,1,2...):
    // adding a new constant in the middle would silently change the meaning of existing rows!
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Genre genre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "published_year", nullable = false)   // Java: publishedYear  <->  SQL: published_year
    private int publishedYear;

    private int pages;

    private boolean available;

    protected Book() {
    }

    public Book(String title, String author, String isbn, Genre genre, BigDecimal price,
                int publishedYear, int pages, boolean available) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.price = price;
        this.publishedYear = publishedYear;
        this.pages = pages;
        this.available = available;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public Genre getGenre() { return genre; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getPublishedYear() { return publishedYear; }
    public int getPages() { return pages; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author + "', genre=" + genre + "}";
    }
}
