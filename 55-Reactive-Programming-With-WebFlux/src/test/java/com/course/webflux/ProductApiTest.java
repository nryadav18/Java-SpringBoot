package com.course.webflux;

/*
 * Integration test: starts the whole app (Netty on a random port + H2 via R2DBC)
 * and calls it with WebTestClient - the reactive cousin of MockMvc.
 * It also uses StepVerifier directly on the reactive repository and SSE stream.
 */
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.course.webflux.product.Product;
import com.course.webflux.product.ProductRepository;
import com.course.webflux.stream.PriceTick;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductApiTest {

    @Autowired
    private WebTestClient webTestClient;   // auto-configured for RANDOM_PORT tests

    @Autowired
    private ProductRepository repository;

    @Test
    void repositoryFindsSeededElectronics() {
        StepVerifier.create(repository.findByCategory("electronics").map(Product::getName))
                .expectNext("Laptop", "Headphones")
                .verifyComplete();
    }

    @Test
    void getProductById() {
        webTestClient.get().uri("/api/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Laptop");
    }

    @Test
    void missingProductGives404() {
        webTestClient.get().uri("/api/products/9999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createProduct() {
        Product created = webTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new Product(null, "Desk Lamp", "Home", new BigDecimal("19.99")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class)
                .returnResult().getResponseBody();

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getCategory()).isEqualTo("home");
    }

    @Test
    void functionalRouteSaysHello() {
        webTestClient.get().uri("/fn/hello?name=Ravi")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Hello, Ravi"));
    }

    @Test
    void serverSentEventsStreamFirstTwoTicks() {
        Flux<PriceTick> ticks = webTestClient.get().uri("/api/stream/prices")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(PriceTick.class)
                .getResponseBody();

        StepVerifier.create(ticks.take(2))
                .assertNext(t -> assertThat(t.sequence()).isZero())
                .assertNext(t -> assertThat(t.sequence()).isEqualTo(1))
                .verifyComplete();
    }
}
