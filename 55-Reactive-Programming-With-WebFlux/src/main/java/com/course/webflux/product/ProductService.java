package com.course.webflux.product;

/*
 * Business logic, written in the reactive style.
 *
 * Golden rule: a reactive method returns a Mono/Flux "recipe" immediately.
 * The real work happens later, when WebFlux subscribes to it.
 * NEVER call .block() inside a reactive app - it freezes an event-loop thread.
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Flux<Product> findAll() {
        return repository.findAll();
    }

    public Mono<Product> findById(Long id) {
        return repository.findById(id)
                // findById gives an EMPTY Mono when nothing is found.
                // switchIfEmpty lets us turn "empty" into a 404 error.
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found")));
    }

    public Flux<Product> findByCategory(String category) {
        return repository.findByCategory(category.toLowerCase());
    }

    public Mono<Product> create(Product product) {
        product.setId(null); // make sure save() INSERTs a new row
        if (product.getCategory() != null) {
            product.setCategory(product.getCategory().toLowerCase());
        }
        return repository.save(product);
    }

    public Mono<Void> delete(Long id) {
        // flatMap: "when the product arrives, call another async method".
        return findById(id).flatMap(repository::delete);
    }

    /*
     * Pretend we have a slow "stock service". In real life this would be a WebClient
     * call to another microservice. delayElement simulates network time WITHOUT
     * blocking a thread (the thread is free to serve others while we wait).
     */
    public Mono<Integer> stockFor(Long productId) {
        return Mono.just((int) (productId * 7 % 20))
                .delayElement(Duration.ofMillis(300));
    }

    /*
     * Mono.zip runs both Monos at the SAME time and combines them when BOTH are ready.
     * Total time = max(time1, time2) instead of time1 + time2.
     */
    public Mono<ProductDetails> details(Long id) {
        return Mono.zip(findById(id), stockFor(id))
                .map(tuple -> new ProductDetails(tuple.getT1(), tuple.getT2()));
    }

    /*
     * Pure helper used by the tests: apply a discount to every product price.
     * map = transform each element synchronously (1 in -> 1 out).
     */
    public static Flux<Product> withDiscount(Flux<Product> products, int percent) {
        BigDecimal factor = BigDecimal.valueOf(100 - percent).divide(BigDecimal.valueOf(100));
        return products.map(p -> new Product(p.getId(), p.getName(), p.getCategory(),
                p.getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP)));
    }
}
