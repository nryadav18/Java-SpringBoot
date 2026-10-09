package com.course.jpaqueries.repository;

import com.course.jpaqueries.entity.Book;
import com.course.jpaqueries.entity.Genre;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/*
 * BookSpecifications - small reusable "WHERE-clause building blocks".
 * -------------------------------------------------------------------
 * A Specification<Book> is one condition. You can combine them with and()/or()
 * at RUNTIME - perfect for search forms where the user fills in only SOME filters.
 *
 * Each method receives:
 *   root  -> the Book being queried (root.get("genre") = the genre field)
 *   query -> the whole query (rarely needed)
 *   cb    -> CriteriaBuilder: a factory for conditions (equal, like, lessThanOrEqualTo ...)
 *
 * Used by BookService.filter(...) through repository.findAll(spec) (from JpaSpecificationExecutor).
 */
public final class BookSpecifications {

    private BookSpecifications() {
    }

    public static Specification<Book> hasGenre(Genre genre) {
        return (root, query, cb) -> cb.equal(root.get("genre"), genre);
    }

    public static Specification<Book> titleContains(String text) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + text.toLowerCase() + "%");
    }

    public static Specification<Book> priceAtMost(BigDecimal maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Book> isAvailable(boolean available) {
        return (root, query, cb) -> cb.equal(root.get("available"), available);
    }
}
