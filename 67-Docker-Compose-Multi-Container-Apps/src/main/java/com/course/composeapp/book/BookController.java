package com.course.composeapp.book;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
 * BookController.java - a small REST API.
 *
 *   GET  /api/books        -> list all books
 *   POST /api/books        -> add a book
 *   GET  /api/books/where  -> shows WHICH database URL the app is using
 *                             (proves that inside Compose we really talk to MySQL)
 *
 * (We call the repository directly to keep this Docker lesson focused.
 *  In a real app put a service layer in between - topic 33.)
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository repository;
    private final String datasourceUrl;

    public BookController(BookRepository repository,
                          @Value("${spring.datasource.url}") String datasourceUrl) {
        this.repository = repository;
        this.datasourceUrl = datasourceUrl;
    }

    @GetMapping
    public List<Book> findAll() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@Valid @RequestBody BookRequest request) {
        return repository.save(new Book(request.title(), request.author()));
    }

    @GetMapping("/where")
    public Map<String, Object> where() {
        // Inside Compose this shows jdbc:mysql://mysql:3306/librarydb
        // "mysql" is the SERVICE NAME from docker-compose.yml, used as a hostname!
        return Map.of("datasourceUrl", datasourceUrl, "bookCount", repository.count());
    }
}
