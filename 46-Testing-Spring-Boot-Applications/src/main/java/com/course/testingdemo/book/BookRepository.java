package com.course.testingdemo.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/*
 * Spring Data repository. The built-in methods (save, findById ...) are already
 * tested by the Spring team - we test OUR custom queries in BookRepositoryTest.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    List<Book> findByAuthorContainingIgnoreCaseOrderByTitleAsc(String author);

    @Query("SELECT b FROM Book b WHERE b.price <= :maxPrice ORDER BY b.price ASC")
    List<Book> findAffordable(@Param("maxPrice") BigDecimal maxPrice);
}
