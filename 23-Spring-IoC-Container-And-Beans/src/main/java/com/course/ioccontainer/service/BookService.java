/*
 * @Service - the stereotype for BUSINESS LOGIC classes.
 *
 * Technically @Service is just a @Component with a more meaningful name.
 * It tells every reader: "the business rules live here".
 *
 * Dependencies (injected by Spring through the constructor):
 *   - BookRepository  -> found by component scanning (@Repository)
 *   - FineCalculator  -> found by component scanning (@Component)
 *   - Clock           -> defined with @Bean in AppConfig (a JDK class)
 *
 * Notice: we write NO "new BookRepository()" here. Spring hands us the beans.
 */
package com.course.ioccontainer.service;

import com.course.ioccontainer.model.Book;
import com.course.ioccontainer.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookService {

    private static final int LOAN_DAYS = 14;

    private final BookRepository bookRepository;
    private final FineCalculator fineCalculator;
    private final Clock clock;

    public BookService(BookRepository bookRepository, FineCalculator fineCalculator, Clock clock) {
        System.out.println("   [container] creating BookService (got its 3 dependencies)");
        this.bookRepository = bookRepository;
        this.fineCalculator = fineCalculator;
        this.clock = clock;
    }

    public List<Book> availableBooks() {
        return bookRepository.findAll().stream().filter(Book::available).toList();
    }

    /** Returns a human-readable message about borrowing the book. */
    public String borrow(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(book -> book.available()
                        ? "Borrowed '" + book.title() + "'. Please return by " + LocalDate.now(clock).plusDays(LOAN_DAYS)
                        : "Sorry, '" + book.title() + "' is already borrowed.")
                .orElse("No book with ISBN " + isbn);
    }

    public String lateFee(long daysLate) {
        return fineCalculator.fineFor(daysLate);
    }
}
