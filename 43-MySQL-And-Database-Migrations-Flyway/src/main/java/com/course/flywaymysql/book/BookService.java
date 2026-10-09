package com.course.flywaymysql.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Business logic layer. Notice: NOTHING here knows whether the database is
 * H2 or MySQL. That is the whole point - the database is a configuration detail.
 */
@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<BookResponse> findAll(String author) {
        List<Book> books = (author == null || author.isBlank())
                ? repository.findAll()
                : repository.findByAuthorContainingIgnoreCase(author);
        return books.stream().map(BookResponse::from).toList();
    }

    public BookResponse findById(Long id) {
        return repository.findById(id)
                .map(BookResponse::from)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        Book book = new Book(request.title(), request.author(), request.price(),
                request.publishedYear(), request.isbn());
        return BookResponse.from(repository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
