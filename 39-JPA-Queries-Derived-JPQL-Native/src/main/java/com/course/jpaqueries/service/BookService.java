package com.course.jpaqueries.service;

import com.course.jpaqueries.dto.AuthorStats;
import com.course.jpaqueries.dto.BookResponse;
import com.course.jpaqueries.dto.BookSummary;
import com.course.jpaqueries.dto.GenreCount;
import com.course.jpaqueries.entity.Book;
import com.course.jpaqueries.entity.Genre;
import com.course.jpaqueries.exception.ResourceNotFoundException;
import com.course.jpaqueries.repository.BookRepository;
import com.course.jpaqueries.repository.BookSpecifications;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * BookService - calls the different kinds of repository queries and maps entities to DTOs.
 * All reads run in a read-only transaction; the two bulk operations need a writable one.
 */
@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    // ---------- helpers ----------
    private static List<BookResponse> toResponses(List<Book> books) {
        return books.stream().map(BookResponse::from).toList();
    }

    // ---------- basic ----------
    public List<BookResponse> findAll() {
        return toResponses(repository.findAll());
    }

    public BookResponse findById(Long id) {
        return repository.findById(id)
                .map(BookResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found"));
    }

    // ---------- A. derived queries ----------
    public List<BookResponse> byAuthor(String author) {
        return toResponses(repository.findByAuthor(author));
    }

    public List<BookResponse> titleContains(String text) {
        return toResponses(repository.findByTitleContainingIgnoreCase(text));
    }

    public List<BookResponse> priceBetween(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min must not be greater than max");
        }
        return toResponses(repository.findByPriceBetween(min, max));
    }

    public List<BookResponse> byGenreCheapestFirst(Genre genre) {
        return toResponses(repository.findByGenreOrderByPriceAsc(genre));
    }

    public List<BookResponse> publishedSince(int year) {
        return toResponses(repository.findByPublishedYearGreaterThanEqualOrderByPublishedYearDesc(year));
    }

    public List<BookResponse> top3MostExpensive() {
        return toResponses(repository.findTop3ByOrderByPriceDesc());
    }

    public BookResponse oldest() {
        // Optional in action: map if present, otherwise throw.
        return repository.findFirstByOrderByPublishedYearAsc()
                .map(BookResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("The library is empty"));
    }

    public BookResponse byIsbn(String isbn) {
        return repository.findByIsbn(isbn)
                .map(BookResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("No book with ISBN " + isbn));
    }

    public boolean existsByIsbn(String isbn) {
        return repository.existsByIsbn(isbn);
    }

    public long countByGenre(Genre genre) {
        return repository.countByGenre(genre);
    }

    // ---------- B. JPQL ----------
    public List<BookResponse> keywordSearch(String keyword) {
        return toResponses(repository.searchByKeyword(keyword));
    }

    public List<BookResponse> thickBooks(int minPages) {
        return toResponses(repository.findThickBooks(minPages));
    }

    // ---------- C. native ----------
    public List<BookResponse> publishedBetween(int from, int to) {
        return toResponses(repository.findPublishedBetweenNative(from, to));
    }

    public List<GenreCount> genreCounts() {
        return repository.countBooksPerGenre();
    }

    // ---------- D. @Modifying (needs a WRITE transaction) ----------
    @Transactional
    public Map<String, Object> applyPriceFactor(Genre genre, BigDecimal factor) {
        if (factor.signum() <= 0) {
            throw new IllegalArgumentException("factor must be positive");
        }
        int updated = repository.applyPriceFactor(genre, factor);
        return Map.of("genre", genre, "factor", factor, "booksUpdated", updated);
    }

    @Transactional
    public Map<String, Integer> deleteUnavailable() {
        return Map.of("booksDeleted", repository.deleteUnavailableBooks());
    }

    // ---------- E. projections ----------
    public List<BookSummary> summariesByGenre(Genre genre) {
        return repository.findSummariesByGenre(genre);
    }

    public List<AuthorStats> authorStats() {
        return repository.authorStatistics();
    }

    public List<BookSummary> summariesByAuthor(String author) {
        return repository.findByAuthorIgnoreCase(author, BookSummary.class);   // dynamic projection
    }

    // ---------- Specifications: combine only the filters the user actually sent ----------
    public List<BookResponse> filter(Genre genre, String title, BigDecimal maxPrice, Boolean available) {
        List<Specification<Book>> specs = new ArrayList<>();
        if (genre != null) specs.add(BookSpecifications.hasGenre(genre));
        if (title != null && !title.isBlank()) specs.add(BookSpecifications.titleContains(title));
        if (maxPrice != null) specs.add(BookSpecifications.priceAtMost(maxPrice));
        if (available != null) specs.add(BookSpecifications.isAvailable(available));

        // allOf(...) joins them with AND. An empty list means "no WHERE clause" = all books.
        return toResponses(repository.findAll(Specification.allOf(specs)));
    }

    // ---------- Query by Example: "find books that look like this one" ----------
    public List<BookResponse> findByExample(String author, String title) {
        Book probe = new Book(title, author, null, null, null, 0, 0, false);  // the "example"

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()                                      // null fields = "don't care"
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)  // LIKE %value%
                .withIgnorePaths("publishedYear", "pages", "available");     // primitives are never null -> ignore

        return toResponses(repository.findAll(Example.of(probe, matcher)));
    }
}
