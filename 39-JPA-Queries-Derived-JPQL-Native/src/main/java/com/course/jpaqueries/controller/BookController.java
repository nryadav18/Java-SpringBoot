package com.course.jpaqueries.controller;

import com.course.jpaqueries.dto.AuthorStats;
import com.course.jpaqueries.dto.BookResponse;
import com.course.jpaqueries.dto.BookSummary;
import com.course.jpaqueries.dto.GenreCount;
import com.course.jpaqueries.entity.Genre;
import com.course.jpaqueries.service.BookService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * BookController - one endpoint per query style. See requests.http for ready-made calls.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BookResponse one(@PathVariable Long id) {
        return service.findById(id);
    }

    // ---------------- A. derived queries ----------------
    @GetMapping("/by-author")                       // /api/books/by-author?name=J.R.R. Tolkien
    public List<BookResponse> byAuthor(@RequestParam String name) {
        return service.byAuthor(name);
    }

    @GetMapping("/title-contains")                  // /api/books/title-contains?text=java
    public List<BookResponse> titleContains(@RequestParam String text) {
        return service.titleContains(text);
    }

    @GetMapping("/price-range")                     // /api/books/price-range?min=10&max=20
    public List<BookResponse> priceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return service.priceBetween(min, max);
    }

    @GetMapping("/genre/{genre}")                   // /api/books/genre/PROGRAMMING
    public List<BookResponse> byGenre(@PathVariable Genre genre) {
        return service.byGenreCheapestFirst(genre);
    }

    @GetMapping("/published-since/{year}")          // /api/books/published-since/2010
    public List<BookResponse> publishedSince(@PathVariable int year) {
        return service.publishedSince(year);
    }

    @GetMapping("/top-expensive")
    public List<BookResponse> topExpensive() {
        return service.top3MostExpensive();
    }

    @GetMapping("/oldest")
    public BookResponse oldest() {
        return service.oldest();
    }

    @GetMapping("/isbn/{isbn}")
    public BookResponse byIsbn(@PathVariable String isbn) {
        return service.byIsbn(isbn);
    }

    @GetMapping("/exists")                          // /api/books/exists?isbn=978-0132350884
    public Map<String, Boolean> exists(@RequestParam String isbn) {
        return Map.of("exists", service.existsByIsbn(isbn));
    }

    @GetMapping("/count")                           // /api/books/count?genre=FANTASY
    public Map<String, Object> count(@RequestParam Genre genre) {
        return Map.of("genre", genre, "count", service.countByGenre(genre));
    }

    // ---------------- B. JPQL ----------------
    @GetMapping("/search")                          // /api/books/search?keyword=tolkien
    public List<BookResponse> search(@RequestParam String keyword) {
        return service.keywordSearch(keyword);
    }

    @GetMapping("/thick")                           // /api/books/thick?minPages=500
    public List<BookResponse> thick(@RequestParam(defaultValue = "500") int minPages) {
        return service.thickBooks(minPages);
    }

    // ---------------- C. native SQL ----------------
    @GetMapping("/published-between")               // /api/books/published-between?from=1940&to=1990
    public List<BookResponse> publishedBetween(@RequestParam int from, @RequestParam int to) {
        return service.publishedBetween(from, to);
    }

    @GetMapping("/stats/genres")
    public List<GenreCount> genreStats() {
        return service.genreCounts();
    }

    // ---------------- D. @Modifying ----------------
    @PutMapping("/genre/{genre}/price-factor")      // PUT /api/books/genre/FANTASY/price-factor?factor=1.10
    public Map<String, Object> priceFactor(@PathVariable Genre genre, @RequestParam BigDecimal factor) {
        return service.applyPriceFactor(genre, factor);
    }

    @DeleteMapping("/unavailable")
    public Map<String, Integer> deleteUnavailable() {
        return service.deleteUnavailable();
    }

    // ---------------- E. projections ----------------
    @GetMapping("/summaries")                       // /api/books/summaries?genre=HISTORY
    public List<BookSummary> summaries(@RequestParam Genre genre) {
        return service.summariesByGenre(genre);
    }

    @GetMapping("/summaries/by-author")             // /api/books/summaries/by-author?name=j.r.r. tolkien
    public List<BookSummary> summariesByAuthor(@RequestParam String name) {
        return service.summariesByAuthor(name);
    }

    @GetMapping("/stats/authors")
    public List<AuthorStats> authorStats() {
        return service.authorStats();
    }

    // ---------------- Specifications & Query by Example ----------------
    @GetMapping("/filter")                          // /api/books/filter?genre=PROGRAMMING&maxPrice=48&available=true
    public List<BookResponse> filter(@RequestParam(required = false) Genre genre,
                                     @RequestParam(required = false) String title,
                                     @RequestParam(required = false) BigDecimal maxPrice,
                                     @RequestParam(required = false) Boolean available) {
        return service.filter(genre, title, maxPrice, available);
    }

    @GetMapping("/example")                         // /api/books/example?author=martin
    public List<BookResponse> byExample(@RequestParam(required = false) String author,
                                        @RequestParam(required = false) String title) {
        return service.findByExample(author, title);
    }
}
