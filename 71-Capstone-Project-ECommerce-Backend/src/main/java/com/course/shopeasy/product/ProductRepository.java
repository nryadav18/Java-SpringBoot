package com.course.shopeasy.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

/**
 * Data access for products (topics 38, 39, 41, 42).
 *
 * JpaSpecificationExecutor adds findAll(Specification, Pageable): dynamic WHERE clauses built from
 * whatever search filters the client sent (see ProductSpecifications).
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Same as the inherited method, plus @EntityGraph: load the category in the SAME query (a join).
     * Without it, showing 10 products with their category name costs 1 + 10 queries - the "N+1 problem" (topic 40).
     */
    @Override
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndActiveTrue(Long id);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsByCategoryId(Long categoryId);

    /**
     * SELECT ... FOR UPDATE - locks the product rows until our transaction ends (pessimistic locking, topic 42).
     *
     * Used when placing / cancelling an order: two customers buying the LAST item at the same moment
     * are processed one after the other, so stock can never go below zero.
     * Sorting by id makes every transaction lock rows in the same order, which prevents deadlocks.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids order by p.id")
    List<Product> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);
}
