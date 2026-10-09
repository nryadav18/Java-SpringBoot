package com.course.testingdemo.book;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/*
 * REST endpoints:
 *   GET    /api/books
 *   GET    /api/books/{id}
 *   POST   /api/books                         -> 201 + Location
 *   PATCH  /api/books/{id}/discount?percent=10
 *   DELETE /api/books/{id}                    -> 204
 *
 * The controller is thin on purpose: it translates HTTP <-> Java and calls the service.
 * BookControllerTest checks exactly that translation (status codes, JSON, validation).
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        BookResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}/discount")
    public BookResponse applyDiscount(@PathVariable Long id, @RequestParam int percent) {
        return service.applyDiscount(id, percent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
