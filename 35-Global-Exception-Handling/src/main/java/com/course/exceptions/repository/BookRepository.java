package com.course.exceptions.repository;

import com.course.exceptions.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
 * In-memory book storage (see topic 33 for the interface-based version).
 * Starts with three sample books.
 */
@Repository
public class BookRepository {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public BookRepository() {
        save(new Book(null, "Clean Code", "Robert C. Martin", "9780132350884", 2));
        save(new Book(null, "Effective Java", "Joshua Bloch", "9780134685991", 1));
        save(new Book(null, "Head First Java", "Kathy Sierra", "9781491910771", 0));
    }

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(ids.incrementAndGet());
        }
        books.put(book.getId(), book);
        return book;
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    public List<Book> findAll() {
        List<Book> all = new ArrayList<>(books.values());
        all.sort(Comparator.comparing(Book::getId));
        return all;
    }

    public boolean existsByIsbn(String isbn) {
        return books.values().stream().anyMatch(b -> b.getIsbn().equals(isbn));
    }

    public void deleteById(Long id) {
        books.remove(id);
    }
}
