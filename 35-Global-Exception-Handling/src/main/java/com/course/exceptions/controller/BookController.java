package com.course.exceptions.controller;

import com.course.exceptions.dto.BookRequest;
import com.course.exceptions.dto.BookResponse;
import com.course.exceptions.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * BookController - notice there is NOT A SINGLE try/catch here.
 * Every failure (validation, not found, business rule, unexpected bug)
 * travels up as an exception and is converted to JSON by the @RestControllerAdvice.
 *
 * Endpoints:
 *   GET    /api/books                 list
 *   GET    /api/books/{id}            one          (404 if missing, 400 if id is not a number)
 *   POST   /api/books                 create       (400 validation, 409 duplicate ISBN, 400 bad JSON)
 *   POST   /api/books/{id}/borrow     borrow       (422 if no copies left)
 *   DELETE /api/books/{id}            delete       (404 if missing)
 *   GET    /api/books/available?minCopies=1       (400 if minCopies < 1)
 *   GET    /api/books/crash           simulates a BUG -> 500 without leaking details
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponse> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookResponse getOne(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @PostMapping("/{id}/borrow")
    public BookResponse borrow(@PathVariable Long id) {
        return bookService.borrow(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public List<BookResponse> available(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "minCopies must be at least 1") int minCopies) {
        return bookService.findAll().stream()
                .filter(b -> b.copiesAvailable() >= minCopies)
                .toList();
    }

    @GetMapping("/crash")
    public BookResponse crash() {
        // Simulates a programming bug deep inside the app (e.g. a NullPointerException).
        String notLoaded = null;
        return new BookResponse(0L, notLoaded.toUpperCase(), "", "", 0);
    }
}
