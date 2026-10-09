package com.course.exceptions.service;

import com.course.exceptions.dto.BookRequest;
import com.course.exceptions.dto.BookResponse;
import com.course.exceptions.exception.BookNotAvailableException;
import com.course.exceptions.exception.DuplicateResourceException;
import com.course.exceptions.exception.ResourceNotFoundException;
import com.course.exceptions.model.Book;
import com.course.exceptions.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * BookService - business logic. Look how CLEAN it is:
 *   - no try/catch
 *   - no ResponseEntity / status codes
 *   - it simply THROWS a meaningful exception when something is wrong.
 * Turning those exceptions into HTTP responses is the job of ONE central
 * class: GlobalExceptionHandler.
 */
@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<BookResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public BookResponse findById(Long id) {
        return toResponse(getBook(id));
    }

    public BookResponse create(BookRequest request) {
        if (repository.existsByIsbn(request.isbn())) {
            throw new DuplicateResourceException("A book with ISBN " + request.isbn() + " already exists");
        }
        Book saved = repository.save(new Book(null, request.title(), request.author(),
                request.isbn(), request.copies()));
        return toResponse(saved);
    }

    public BookResponse borrow(Long id) {
        Book book = getBook(id);                               // 404 if missing
        if (book.getCopiesAvailable() == 0) {
            throw new BookNotAvailableException(book.getTitle()); // 422 business rule
        }
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        return toResponse(repository.save(book));
    }

    public void delete(Long id) {
        getBook(id);                                           // 404 if missing
        repository.deleteById(id);
    }

    private Book getBook(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    private BookResponse toResponse(Book b) {
        return new BookResponse(b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                b.getCopiesAvailable());
    }
}
