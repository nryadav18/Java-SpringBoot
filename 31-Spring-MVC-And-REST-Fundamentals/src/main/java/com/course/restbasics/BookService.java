package com.course.restbasics;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

/*
 * Keeps books in a simple in-memory List (no database yet - that comes in topics 37-38).
 *
 * Why CopyOnWriteArrayList and AtomicLong?
 *   A web server handles many requests AT THE SAME TIME on different threads (topic 17).
 *   These two classes are thread-safe, so two requests adding books together cannot corrupt the list
 *   or get the same id. A normal ArrayList / long counter would NOT be safe here.
 *
 * NOTE: everything is lost when the app stops. That is fine for learning.
 */
@Service
public class BookService {

    private final List<Book> books = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public BookService() {
        // Some sample data so GET /api/books is not empty on first run.
        add(new Book(null, "Clean Code", "Robert C. Martin", 2008, 499.0));
        add(new Book(null, "Effective Java", "Joshua Bloch", 2018, 650.0));
        add(new Book(null, "Head First Java", "Kathy Sierra", 2022, 550.0));
        add(new Book(null, "Spring in Action", "Craig Walls", 2022, 720.0));
    }

    public List<Book> findAll() {
        return List.copyOf(books);   // return a read-only copy, so callers cannot change our list
    }

    public Optional<Book> findById(long id) {
        return books.stream().filter(b -> b.id() == id).findFirst();
    }

    public List<Book> findByAuthor(String author) {
        return books.stream()
                .filter(b -> b.author().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    public Book add(Book book) {
        Book saved = book.withId(nextId.getAndIncrement());   // the SERVER decides the id, not the client
        books.add(saved);
        return saved;
    }

    public boolean deleteById(long id) {
        return books.removeIf(b -> b.id() == id);
    }
}
