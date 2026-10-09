package com.course.flywaymysql.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * JPA entity mapped to the "books" table.
 *
 * IMPORTANT: this class does NOT create the table any more!
 * The table is created by Flyway (V1__create_books_table.sql) and changed by
 * V3__add_isbn_column.sql. Hibernate only VALIDATES (ddl-auto=validate) that
 * every field below matches a real column. If you add a field here but forget
 * the migration, the app refuses to start - that is a GOOD thing: you find the
 * mistake on your laptop, not in production.
 */
@Entity
@Table(name = "books")
public class Book {

    // IDENTITY = "let the database generate the id" (AUTO_INCREMENT in MySQL and H2)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    // DECIMAL(10,2) in SQL. Always use BigDecimal for money, never double.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "published_year")
    private Integer publishedYear;

    // Added later by migration V3. Old rows may have NULL here.
    @Column(length = 20)
    private String isbn;

    // Filled automatically by Hibernate when we save a NEW book.
    // (Rows inserted by the V2 script get the database DEFAULT CURRENT_TIMESTAMP.)
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // JPA needs a no-argument constructor (it can be protected).
    protected Book() {
    }

    public Book(String title, String author, BigDecimal price, Integer publishedYear, String isbn) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.publishedYear = publishedYear;
        this.isbn = isbn;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }
    public Integer getPublishedYear() { return publishedYear; }
    public String getIsbn() { return isbn; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
