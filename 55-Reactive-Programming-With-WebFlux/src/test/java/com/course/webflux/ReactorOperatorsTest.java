package com.course.webflux;

/*
 * Pure Reactor tests with StepVerifier (no Spring needed - very fast).
 *
 * StepVerifier = "a robot subscriber". It subscribes to a Mono/Flux and checks,
 * step by step, what comes out: expectNext(...), expectError(...), verifyComplete().
 * IMPORTANT: if you forget verify()/verifyComplete(), NOTHING is checked!
 *
 * Run: mvn test
 */
import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.course.webflux.product.Product;
import com.course.webflux.product.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ReactorOperatorsTest {

    @Test
    @DisplayName("map + filter emit the expected values and then complete")
    void mapAndFilter() {
        Flux<Integer> squaresOfEven = Flux.range(1, 6)
                .filter(n -> n % 2 == 0)
                .map(n -> n * n);

        StepVerifier.create(squaresOfEven)
                .expectNext(4, 16, 36)
                .verifyComplete();
    }

    @Test
    @DisplayName("an error stops the stream; onErrorResume provides a fallback")
    void errorAndFallback() {
        Flux<Integer> division = Flux.just(10, 0, 5).map(n -> 100 / n);

        StepVerifier.create(division)
                .expectNext(10)
                .expectError(ArithmeticException.class)
                .verify();

        StepVerifier.create(division.onErrorResume(ex -> Mono.just(-1)))
                .expectNext(10, -1)
                .verifyComplete();
    }

    @Test
    @DisplayName("zip combines two sources pair by pair")
    void zip() {
        Mono<String> combined = Mono.zip(Mono.just("Laptop"), Mono.just(3))
                .map(t -> t.getT1() + " x" + t.getT2());

        StepVerifier.create(combined)
                .expectNext("Laptop x3")
                .verifyComplete();
    }

    @Test
    @DisplayName("virtual time: test a 1-hour stream in milliseconds")
    void virtualTime() {
        // withVirtualTime lets us "fast forward" the clock instead of really waiting.
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofMinutes(20)).take(3))
                .expectSubscription()
                .thenAwait(Duration.ofHours(1))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("withDiscount reduces every price by the given percent")
    void discount() {
        Flux<Product> products = Flux.just(
                new Product(1L, "Laptop", "electronics", new BigDecimal("1000.00")),
                new Product(2L, "Mug", "kitchen", new BigDecimal("10.00")));

        StepVerifier.create(ProductService.withDiscount(products, 10))
                .assertNext(p -> org.assertj.core.api.Assertions.assertThat(p.getPrice()).isEqualByComparingTo("900.00"))
                .assertNext(p -> org.assertj.core.api.Assertions.assertThat(p.getPrice()).isEqualByComparingTo("9.00"))
                .verifyComplete();
    }
}
