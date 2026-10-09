package com.course.webflux.functional;

/*
 * STYLE 2: Functional endpoints.
 * A "handler" is a plain class with methods: ServerRequest in -> Mono<ServerResponse> out.
 * The URL mapping lives separately in RouterConfig (no @GetMapping here).
 */
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.course.webflux.product.Product;
import com.course.webflux.product.ProductService;

import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

    private final ProductService productService;

    public GreetingHandler(ProductService productService) {
        this.productService = productService;
    }

    // GET /fn/hello?name=Asha
    public Mono<ServerResponse> hello(ServerRequest request) {
        String name = request.queryParam("name").orElse("learner");
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Hello, " + name + "! This answer came from a functional route.");
    }

    // GET /fn/products
    public Mono<ServerResponse> products(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findAll(), Product.class);
    }
}
