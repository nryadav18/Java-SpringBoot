package com.course.jpaqueries.repository;

import com.course.jpaqueries.dto.AuthorStats;
import com.course.jpaqueries.dto.BookSummary;
import com.course.jpaqueries.dto.GenreCount;
import com.course.jpaqueries.entity.Book;
import com.course.jpaqueries.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/*
 * BookRepository - FIVE ways to query, all in one place.
 * =====================================================
 *  A. Derived query methods   -> Spring reads the METHOD NAME and builds the query.
 *  B. @Query with JPQL        -> you write the query using ENTITY/FIELD names.
 *  C. @Query native SQL       -> you write real SQL using TABLE/COLUMN names.
 *  D. @Modifying queries      -> bulk UPDATE / DELETE.
 *  E. Projections             -> return only some columns (interface or record).
 *  (+ Specifications via JpaSpecificationExecutor, + Query by Example - inherited from JpaRepository)
 *
 * Tip: run the app and watch the console - every call prints the SQL Hibernate generated.
 */
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // =====================================================================
    // A. DERIVED QUERY METHODS:  find/exists/count/delete + By + conditions
    // =====================================================================

    // ... WHERE author = ?
    List<Book> findByAuthor(String author);

    // ... WHERE UPPER(title) LIKE UPPER('%' || ? || '%')
    List<Book> findByTitleContainingIgnoreCase(String text);

    // ... WHERE price BETWEEN ? AND ?
    List<Book> findByPriceBetween(BigDecimal min, BigDecimal max);

    // ... WHERE genre = ? ORDER BY price ASC
    List<Book> findByGenreOrderByPriceAsc(Genre genre);

    // ... WHERE published_year >= ? ORDER BY published_year DESC
    List<Book> findByPublishedYearGreaterThanEqualOrderByPublishedYearDesc(int year);

    // ... WHERE author = ? AND available = TRUE      (two conditions joined with And)
    List<Book> findByAuthorAndAvailableTrue(String author);

    // ... WHERE genre IN (?, ?, ...)
    List<Book> findByGenreIn(Collection<Genre> genres);

    // ... ORDER BY price DESC  FETCH FIRST 3 ROWS ONLY     (Top3 = LIMIT 3)
    List<Book> findTop3ByOrderByPriceDesc();

    // The single oldest book. First == Top1. Optional because the table could be empty.
    Optional<Book> findFirstByOrderByPublishedYearAsc();

    Optional<Book> findByIsbn(String isbn);

    // SELECT ... FETCH FIRST 1 ROWS ONLY  -> true/false   (cheaper than loading the book)
    boolean existsByIsbn(String isbn);

    // SELECT COUNT(*) ... WHERE genre = ?
    long countByGenre(Genre genre);

    // =====================================================================
    // B. @Query with JPQL  (Java Persistence Query Language)
    //    "Book" and "b.title" are the ENTITY and FIELD names, not the table/columns!
    // =====================================================================

    // Named parameter  :keyword  bound with @Param("keyword")
    @Query("""
            SELECT b FROM Book b
            WHERE LOWER(b.title)  LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY b.title
            """)
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    // Positional parameter ?1 (first method argument). Works, but named parameters are clearer.
    @Query("SELECT b FROM Book b WHERE b.pages > ?1 ORDER BY b.pages DESC")
    List<Book> findThickBooks(int minPages);

    // =====================================================================
    // C. NATIVE SQL  (nativeQuery = true) - real SQL for the real database
    //    "books" and "published_year" are TABLE and COLUMN names.
    // =====================================================================

    @Query(value = """
            SELECT * FROM books
            WHERE published_year BETWEEN :fromYear AND :toYear
            ORDER BY published_year
            """, nativeQuery = true)
    List<Book> findPublishedBetweenNative(@Param("fromYear") int fromYear, @Param("toYear") int toYear);

    // Native query + interface projection: column ALIASES must match the getter names.
    @Query(value = """
            SELECT genre AS genre, COUNT(*) AS total
            FROM books
            GROUP BY genre
            ORDER BY total DESC
            """, nativeQuery = true)
    List<GenreCount> countBooksPerGenre();

    // =====================================================================
    // D. @Modifying - bulk UPDATE / DELETE in ONE statement
    //    - must run inside a transaction (@Transactional on the service method)
    //    - returns the number of affected rows
    //    - bypasses the persistence context, so clearAutomatically = true throws away
    //      stale managed entities afterwards (otherwise you might read OLD prices)
    // =====================================================================

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Book b SET b.price = b.price * :factor WHERE b.genre = :genre")
    int applyPriceFactor(@Param("genre") Genre genre, @Param("factor") BigDecimal factor);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Book b WHERE b.available = false")
    int deleteUnavailableBooks();

    // =====================================================================
    // E. PROJECTIONS - fetch only what you need
    // =====================================================================

    // Interface projection with a derived query: SELECT b.title, b.author FROM books b WHERE genre = ?
    List<BookSummary> findSummariesByGenre(Genre genre);

    // Record (DTO) projection with a JPQL constructor expression + GROUP BY
    @Query("""
            SELECT new com.course.jpaqueries.dto.AuthorStats(b.author, COUNT(b), AVG(b.price))
            FROM Book b
            GROUP BY b.author
            ORDER BY COUNT(b) DESC, b.author
            """)
    List<AuthorStats> authorStatistics();

    // Dynamic projection: the CALLER chooses the return type, e.g.
    //   findByAuthorIgnoreCase("tolkien", BookSummary.class)  or  (..., Book.class)
    <T> List<T> findByAuthorIgnoreCase(String author, Class<T> type);
}
