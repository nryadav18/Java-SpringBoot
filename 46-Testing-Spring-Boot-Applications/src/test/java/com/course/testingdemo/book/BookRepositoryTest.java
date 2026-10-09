package com.course.testingdemo.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
 * LEVEL 2 - JPA SLICE TEST with @DataJpaTest
 *
 * Starts ONLY the persistence layer: entities, repositories, Hibernate, an
 * in-memory H2 database (replacing any configured DataSource by default).
 * No controllers, no services, no web server.
 *
 * Every test runs in a TRANSACTION that is ROLLED BACK at the end,
 * so tests never see each other's data.
 *
 * TestEntityManager = a test helper to insert data directly (bypassing the repository
 * we are testing) and to flush/clear the persistence context.
 */
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository repository;

    @BeforeEach
    void insertBooks() {
        entityManager.persist(new Book("Clean Code", "Robert C. Martin", "978-0132350884", new BigDecimal("450.00")));
        entityManager.persist(new Book("Clean Architecture", "Robert C. Martin", "978-0134494166", new BigDecimal("480.00")));
        entityManager.persist(new Book("Effective Java", "Joshua Bloch", "978-0134685991", new BigDecimal("599.00")));
        entityManager.flush();   // send the INSERTs to the database now
    }

    @Test
    void findByIsbnFindsTheRightBook() {
        assertThat(repository.findByIsbn("978-0134685991"))
                .isPresent()
                .get()
                .extracting(Book::getTitle)
                .isEqualTo("Effective Java");
    }

    @Test
    void findByIsbnReturnsEmptyForUnknownIsbn() {
        assertThat(repository.findByIsbn("000")).isEmpty();
    }

    @Test
    void existsByIsbnWorks() {
        assertThat(repository.existsByIsbn("978-0132350884")).isTrue();
        assertThat(repository.existsByIsbn("nope")).isFalse();
    }

    @Test
    void findByAuthorIsCaseInsensitiveAndSortedByTitle() {
        List<Book> books = repository.findByAuthorContainingIgnoreCaseOrderByTitleAsc("MARTIN");

        assertThat(books).extracting(Book::getTitle)
                .containsExactly("Clean Architecture", "Clean Code");
    }

    @Test
    void customJpqlQueryReturnsAffordableBooksCheapestFirst() {
        List<Book> books = repository.findAffordable(new BigDecimal("500"));

        assertThat(books).extracting(Book::getTitle)
                .containsExactly("Clean Code", "Clean Architecture");
    }

    @Test
    void uniqueIsbnIsEnforcedByTheDatabase() {
        Book duplicate = new Book("Copy", "Someone", "978-0132350884", new BigDecimal("1.00"));

        // saveAndFlush forces the INSERT now, so the constraint is checked inside the test
        assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
