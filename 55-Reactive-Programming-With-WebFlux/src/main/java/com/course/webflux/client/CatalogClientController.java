package com.course.webflux.client;

/*
 * WebClient demo. This endpoint calls OUR OWN /api/products endpoint over HTTP,
 * as if it were another microservice. So it works offline - no internet needed.
 *
 * Try: curl http://localhost:8080/api/client/summary
 */
import java.math.BigDecimal;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.course.webflux.product.Product;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/client")
public class CatalogClientController {

    private static final Logger log = LoggerFactory.getLogger(CatalogClientController.class);

    private final WebClient webClient;

    public CatalogClientController(WebClient catalogWebClient) {
        this.webClient = catalogWebClient;
    }

    @GetMapping("/summary")
    public Mono<CatalogSummary> summary() {
        return webClient.get()
                .uri("/api/products")
                .retrieve()                           // send the request; 4xx/5xx become errors
                .bodyToFlux(Product.class)            // JSON array -> Flux<Product>
                .timeout(Duration.ofSeconds(3))       // never wait forever
                .map(Product::getPrice)
                .collectList()                        // Flux<BigDecimal> -> Mono<List<BigDecimal>>
                .map(prices -> new CatalogSummary(
                        prices.size(),
                        prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add),
                        "via WebClient"))
                // If the other service is down, return a friendly fallback instead of a 500.
                .onErrorResume(ex -> {
                    log.warn("Catalog call failed: {}", ex.toString());
                    return Mono.just(new CatalogSummary(0, BigDecimal.ZERO, "fallback (catalog unavailable)"));
                });
    }
}
