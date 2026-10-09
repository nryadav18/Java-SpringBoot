package com.course.jpaqueries;

import com.course.jpaqueries.dto.AuthorStats;
import com.course.jpaqueries.dto.BookSummary;
import com.course.jpaqueries.dto.GenreCount;
import com.course.jpaqueries.entity.Book;
import com.course.jpaqueries.entity.Genre;
import com.course.jpaqueries.repository.BookRepository;
import com.course.jpaqueries.repository.BookSpecifications;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * BookRepositoryTest - checks every query style against the 13 books from data.sql.
 * @DataJpaTest loads data.sql too, and rolls back after each test (so the delete/update tests
 * do not disturb the other tests).
 *
 * Run:  mvn test
 */
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("data.sql loaded 13 books")
    void sampleDataLoaded() {
        assertThat(repository.count()).isEqualTo(13);
    }

    // ---------------- derived ----------------
    @Test
    void derivedQueries() {
        assertThat(repository.findByAuthor("J.R.R. Tolkien")).hasSize(2);
        assertThat(repository.findByTitleContainingIgnoreCase("JAVA"))
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Effective Java", "Head First Java", "Java Concurrency in Practice");
        assertThat(repository.findByPriceBetween(new BigDecimal("10"), new BigDecimal("20"))).hasSize(5);
        assertThat(repository.findByAuthorAndAvailableTrue("Brian Goetz")).isEmpty();
        assertThat(repository.findByGenreIn(Set.of(Genre.FICTION, Genre.FANTASY))).hasSize(4);
        assertThat(repository.countByGenre(Genre.PROGRAMMING)).isEqualTo(5);
        assertThat(repository.existsByIsbn("978-0132350884")).isTrue();
        assertThat(repository.existsByIsbn("000")).isFalse();
    }

    @Test
    void orderingTopAndFirst() {
        assertThat(repository.findByGenreOrderByPriceAsc(Genre.PROGRAMMING))
                .first().extracting(Book::getTitle).isEqualTo("Clean Code");
        assertThat(repository.findTop3ByOrderByPriceDesc())
                .extracting(Book::getTitle)
                .containsExactly("Head First Java", "Java Concurrency in Practice", "Refactoring");
        assertThat(repository.findFirstByOrderByPublishedYearAsc())
                .get().extracting(Book::getTitle).isEqualTo("The Hobbit");
    }

    // ---------------- JPQL ----------------
    @Test
    void jpqlQueries() {
        assertThat(repository.searchByKeyword("tolkien")).hasSize(2);
        assertThat(repository.searchByKeyword("history")).extracting(Book::getTitle)
                .containsExactly("A Brief History of Time");
        assertThat(repository.findThickBooks(700)).extracting(Book::getTitle)
                .containsExactly("The Lord of the Rings", "Head First Java");
    }

    // ---------------- native ----------------
    @Test
    void nativeQueries() {
        assertThat(repository.findPublishedBetweenNative(1940, 1960)).extracting(Book::getTitle)
                .containsExactly("1984", "The Lord of the Rings", "To Kill a Mockingbird");

        List<GenreCount> counts = repository.countBooksPerGenre();
        assertThat(counts.get(0).getGenre()).isEqualTo("PROGRAMMING");
        assertThat(counts.get(0).getTotal()).isEqualTo(5);
    }

    // ---------------- @Modifying ----------------
    @Test
    void modifyingUpdateChangesPrices() {
        int updated = repository.applyPriceFactor(Genre.FANTASY, new BigDecimal("2"));

        assertThat(updated).isEqualTo(2);
        // clearAutomatically = true, so this reads FRESH data from the database:
        assertThat(repository.findByIsbn("978-0547928227").orElseThrow().getPrice())
                .isEqualByComparingTo("25.98");
    }

    @Test
    void modifyingDeleteRemovesUnavailableBooks() {
        assertThat(repository.deleteUnavailableBooks()).isEqualTo(2);
        assertThat(repository.count()).isEqualTo(11);
    }

    // ---------------- projections ----------------
    @Test
    void projections() {
        List<BookSummary> history = repository.findSummariesByGenre(Genre.HISTORY);
        assertThat(history).extracting(BookSummary::getTitle)
                .containsExactlyInAnyOrder("Sapiens", "Guns, Germs, and Steel");

        List<AuthorStats> stats = repository.authorStatistics();
        assertThat(stats.get(0).author()).isEqualTo("J.R.R. Tolkien");
        assertThat(stats.get(0).bookCount()).isEqualTo(2L);

        assertThat(repository.findByAuthorIgnoreCase("j.r.r. tolkien", BookSummary.class)).hasSize(2);
    }

    // ---------------- Specifications ----------------
    @Test
    void specificationsCombineFilters() {
        List<Book> result = repository.findAll(
                BookSpecifications.hasGenre(Genre.PROGRAMMING)
                        .and(BookSpecifications.priceAtMost(new BigDecimal("48")))
                        .and(BookSpecifications.isAvailable(true)));

        assertThat(result).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Effective Java", "Refactoring");
    }
}
