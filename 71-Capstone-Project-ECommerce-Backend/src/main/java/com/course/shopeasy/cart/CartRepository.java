package com.course.shopeasy.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Derived query on a nested property "user.id" (topic 39).
     * @EntityGraph loads the cart, its items AND their products with joins in one SQL statement,
     * so computing the totals does not trigger one extra query per line (N+1, topic 40).
     */
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Cart> findByUserId(Long userId);

    /**
     * Only the product ids in a user's cart - a light JPQL query (topic 39).
     * OrderService needs the ids FIRST so it can lock those product rows before reading their stock.
     */
    @Query("select ci.product.id from CartItem ci where ci.cart.user.id = :userId")
    List<Long> findProductIdsInCart(@Param("userId") Long userId);
}
