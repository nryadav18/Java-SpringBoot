package com.course.jparelationships.repository;

import com.course.jparelationships.entity.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/*
 * CustomerRepository - shows the TWO standard fixes for the N+1 problem.
 *
 *   findAll()  (inherited)          -> 1 query for customers, then 1 MORE query per customer
 *                                      the first time you touch customer.getOrders()   = N+1
 *   findAllWithOrders() JOIN FETCH  -> ONE query that loads customers + orders together
 *   findAllWithAddressAndOrders()   -> same idea with @EntityGraph (no JPQL needed)
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    // Fix 1: JOIN FETCH in JPQL. "FETCH" = "load the related entities in the same SELECT".
    // LEFT join so customers WITHOUT orders are included too.
    @Query("SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.address LEFT JOIN FETCH c.orders ORDER BY c.id")
    List<Customer> findAllWithOrders();

    // Fix 2: @EntityGraph - list the relationships to load eagerly FOR THIS QUERY ONLY.
    @EntityGraph(attributePaths = {"address", "orders"})
    @Query("SELECT c FROM Customer c ORDER BY c.id")
    List<Customer> findAllWithAddressAndOrders();

    // One customer with everything the "details" screen needs: address, orders AND each order's products.
    // ("WithDetails" between find and By is just a readable label - Spring ignores it.)
    @EntityGraph(attributePaths = {"address", "orders", "orders.products"})
    Optional<Customer> findWithDetailsById(Long id);
}
