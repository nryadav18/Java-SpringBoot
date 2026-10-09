package com.course.pagination.repository;

import com.course.pagination.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*
 * ProductRepository - paging & sorting support.
 * ---------------------------------------------
 * JpaRepository already gives us:
 *     Page<Product> findAll(Pageable pageable)
 *     List<Product> findAll(Sort sort)
 * JpaSpecificationExecutor adds:
 *     Page<Product> findAll(Specification<Product> spec, Pageable pageable)   <- filters + paging
 *
 * And ANY query method can take a Pageable or Sort as its LAST parameter:
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Page = content + total count. Spring runs TWO queries: the page (LIMIT/OFFSET) and a COUNT(*).
    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

    // Slice = content + "is there a next page?". Only ONE query (it fetches size+1 rows to find out).
    Slice<Product> findSliceByCategoryIgnoreCase(String category, Pageable pageable);

    // Slice over all products (named "findAllBy" so it does not clash with findAll(Pageable)).
    Slice<Product> findAllBy(Pageable pageable);

    // Sorting only, no paging: Sort as the last parameter.
    List<Product> findTop5ByCategoryIgnoreCase(String category, Sort sort);

    // @Query works with Pageable too. Spring derives the COUNT query automatically;
    // for complicated queries you can give your own with countQuery = "...".
    @Query(value = "SELECT p FROM Product p WHERE p.stock < :threshold",
           countQuery = "SELECT COUNT(p) FROM Product p WHERE p.stock < :threshold")
    Page<Product> findLowStock(@Param("threshold") int threshold, Pageable pageable);
}
