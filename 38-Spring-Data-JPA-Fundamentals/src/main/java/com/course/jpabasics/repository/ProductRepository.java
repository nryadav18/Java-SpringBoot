package com.course.jpabasics.repository;

import com.course.jpabasics.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * ProductRepository - the data-access layer.
 * -------------------------------------------
 * Look: it is only an INTERFACE and we never write a class that implements it!
 * At startup Spring Data JPA creates a proxy object that implements it for us.
 *
 * JpaRepository<Product, Long>
 *               ^^^^^^^  ^^^^
 *               entity   type of its @Id field
 *
 * We get these methods for free (and many more):
 *   save(entity)        - INSERT if new, UPDATE if it already exists
 *   saveAll(list)
 *   findById(id)        - returns Optional<Product>
 *   findAll()           - returns List<Product>
 *   existsById(id)      - true / false
 *   count()             - number of rows
 *   deleteById(id), delete(entity), deleteAll()
 *
 * We can also declare our own methods and Spring builds the query from the METHOD NAME
 * (deep dive in topic 39). Two small examples:
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    // SELECT ... FROM products WHERE sku = ?
    Optional<Product> findBySku(String sku);

    // SELECT COUNT(*) > 0 FROM products WHERE sku = ?
    boolean existsBySku(String sku);
}
