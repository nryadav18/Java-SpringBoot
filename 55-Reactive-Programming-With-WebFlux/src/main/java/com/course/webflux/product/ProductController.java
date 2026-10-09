package com.course.webflux.product;

/*
 * STYLE 1: Annotated controller - looks exactly like Spring MVC!
 * The only difference: methods return Mono<T> / Flux<T>.
 *
 * Try:  curl http://localhost:8080/api/products
 */
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // Flux<Product> -> WebFlux writes a JSON array: [ {...}, {...} ]
    @GetMapping
    public Flux<Product> all() {
        return service.findAll();
    }

    // Mono<Product> -> one JSON object (or 404 thanks to switchIfEmpty in the service)
    @GetMapping("/{id}")
    public Mono<Product> one(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/category/{category}")
    public Flux<Product> byCategory(@PathVariable String category) {
        return service.findByCategory(category);
    }

    // Mono.zip demo: product + stock fetched in parallel
    @GetMapping("/{id}/details")
    public Mono<ProductDetails> details(@PathVariable Long id) {
        return service.details(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> create(@RequestBody Product product) {
        return service.create(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
