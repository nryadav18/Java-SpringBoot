package com.course.composeapp.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * Book.java - a JPA entity (one object = one row in the "books" table).
 *
 * Nothing Docker-specific here. The table "books" is created by db/init.sql when the
 * MySQL container starts for the first time (and by Hibernate's ddl-auto=update for H2).
 * In a real project you would use Flyway migrations instead (topic 43).
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL AUTO_INCREMENT
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    // JPA needs a no-argument constructor (it creates objects by reflection).
    protected Book() {
    }

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}
