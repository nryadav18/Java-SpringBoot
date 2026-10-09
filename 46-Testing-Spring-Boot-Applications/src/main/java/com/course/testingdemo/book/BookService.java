package com.course.testingdemo.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/*
 * Business rules live here - so this is the class with the MOST unit tests.
 *
 * Rules:
 *   1. ISBN must be unique                      -> DuplicateIsbnException
 *   2. A discount must be between 1 and 50 %    -> IllegalArgumentException
 *   3. Prices are rounded to 2 decimals (HALF_UP)
 *
 * Constructor injection (topic 24) makes this class EASY to test:
 * in a unit test we simply pass a mock repository into the constructor.
 */
@Service
@Transactional(readOnly = true)
public class BookService {

    static final int MAX_DISCOUNT_PERCENT = 50;

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<BookResponse> findAll() {
        return repository.findAll().stream().map(BookResponse::from).toList();
    }

    public BookResponse findById(Long id) {
        return BookResponse.from(getOrThrow(id));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        if (repository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }
        Book book = new Book(request.title().trim(), request.author().trim(), request.isbn(),
                request.price().setScale(2, RoundingMode.HALF_UP));
        return BookResponse.from(repository.save(book));
    }

    /**
     * Reduces the price by the given percentage, e.g. 10 -> price * 0.90.
     */
    @Transactional
    public BookResponse applyDiscount(Long id, int percent) {
        if (percent < 1 || percent > MAX_DISCOUNT_PERCENT) {
            throw new IllegalArgumentException("Discount must be between 1 and " + MAX_DISCOUNT_PERCENT + " percent");
        }
        Book book = getOrThrow(id);
        BigDecimal factor = BigDecimal.valueOf(100 - percent).divide(BigDecimal.valueOf(100));
        book.setPrice(book.getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        return BookResponse.from(repository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Book getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }
}
