package com.course.shopeasy.order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access for orders.
 *
 * Paging + collections: we must NOT "JOIN FETCH" the items in a paged query (Hibernate would load ALL rows
 * and page in memory). Instead the items are loaded lazily in batches thanks to
 * hibernate.default_batch_fetch_size=50 in application.yml - 1 query for the page + 1 for all items.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** A customer's own orders (derived query on user.id). */
    @EntityGraph(attributePaths = "user")
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /** Admin view filtered by status; loads the user in the same query for the customer e-mail. */
    @EntityGraph(attributePaths = "user")
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /** Admin view of all orders. */
    @Override
    @EntityGraph(attributePaths = "user")
    Page<Order> findAll(Pageable pageable);

    /** One order with its user and items. "Detailed" is just a readable word between find...By. */
    @EntityGraph(attributePaths = {"user", "items"})
    Optional<Order> findDetailedById(Long id);
}
