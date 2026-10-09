package com.course.graphql.library;

/*
 * A simple IN-MEMORY repository (no database) so we can focus on GraphQL.
 * In a real app this would be two Spring Data JPA repositories (Topic 38).
 *
 * ConcurrentHashMap + AtomicLong = safe when several requests arrive at once.
 */
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
public class LibraryRepository {

    private final Map<Long, Author> authors = new ConcurrentHashMap<>();
    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong bookIds = new AtomicLong();

    // Counts how many times the author table was "queried" - used to SEE the N+1 problem.
    private final AtomicInteger authorQueries = new AtomicInteger();

    public LibraryRepository() {
        authors.put(1L, new Author(1L, "Joshua Bloch", "USA"));
        authors.put(2L, new Author(2L, "Kathy Sierra", "USA"));
        authors.put(3L, new Author(3L, "Craig Walls", "USA"));

        save(new Book(null, "Effective Java", 412, 1L));
        save(new Book(null, "Java Puzzlers", 312, 1L));
        save(new Book(null, "Head First Java", 720, 2L));
        save(new Book(null, "Head First Design Patterns", 694, 2L));
        save(new Book(null, "Spring in Action", 520, 3L));
    }

    // ---------- books ----------
    public List<Book> findAllBooks() {
        return books.values().stream().sorted(Comparator.comparing(Book::id)).toList();
    }

    public Optional<Book> findBookById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    public List<Book> findBooksByTitle(String text) {
        String lower = text.toLowerCase();
        return findAllBooks().stream().filter(b -> b.title().toLowerCase().contains(lower)).toList();
    }

    public List<Book> findBooksByAuthorId(Long authorId) {
        return findAllBooks().stream().filter(b -> b.authorId().equals(authorId)).toList();
    }

    public Book save(Book book) {
        Long id = book.id() != null ? book.id() : bookIds.incrementAndGet();
        Book saved = new Book(id, book.title(), book.pages(), book.authorId());
        books.put(id, saved);
        return saved;
    }

    public boolean deleteBook(Long id) {
        return books.remove(id) != null;
    }

    // ---------- authors ----------
    public List<Author> findAllAuthors() {
        return authors.values().stream().sorted(Comparator.comparing(Author::id)).toList();
    }

    public Optional<Author> findAuthorById(Long id) {
        authorQueries.incrementAndGet();          // imagine: SELECT * FROM author WHERE id = ?
        return Optional.ofNullable(authors.get(id));
    }

    /* Load MANY authors in ONE go - imagine: SELECT * FROM author WHERE id IN (...) */
    public List<Author> findAuthorsByIds(Collection<Long> ids) {
        authorQueries.incrementAndGet();
        return ids.stream().distinct().map(authors::get).filter(a -> a != null).toList();
    }

    public int authorQueryCount() {
        return authorQueries.get();
    }
}
