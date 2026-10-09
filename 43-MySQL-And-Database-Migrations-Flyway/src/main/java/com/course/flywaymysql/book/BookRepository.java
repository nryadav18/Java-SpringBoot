package com.course.flywaymysql.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 * Spring Data JPA repository (topics 38/39).
 * Works the same on H2 and MySQL - Hibernate generates the right SQL "dialect"
 * for whichever database we are connected to.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    // SELECT * FROM books WHERE LOWER(author) LIKE LOWER('%' || ? || '%')
    List<Book> findByAuthorContainingIgnoreCase(String author);
}
