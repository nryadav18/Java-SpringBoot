package com.course.webflux.product;

/*
 * Reactive repository.
 *
 * It looks just like a JpaRepository, but every method returns
 * Mono (0..1 result) or Flux (0..N results) instead of Optional / List.
 * Nothing touches the database until somebody SUBSCRIBES.
 */
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    // Derived query, exactly like Spring Data JPA - but it returns a Flux.
    Flux<Product> findByCategory(String category);
}
