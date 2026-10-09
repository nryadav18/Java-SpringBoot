/*
 * ProductClient.java  (Topic 59 - order-service)
 *
 * WHAT: The small class that knows HOW to talk to product-service over HTTP.
 *       It uses RestClient (Spring 6.1+), the modern blocking HTTP client (see Topic 45).
 *
 * THE PROBLEM THIS TOPIC WANTS YOU TO NOTICE:
 *       The address "http://localhost:8081" is FIXED (written in application.properties).
 *         - What if product-service moves to another machine or port?
 *         - What if we run 3 copies of product-service for heavy traffic? Which one do we call?
 *         - What if product-service is down? (try it: stop it and place an order)
 *       Topic 60 (Eureka), Topic 61 (Gateway) and Topic 64 (Resilience4j) solve these.
 */
package com.course.msintro.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

    private final RestClient restClient;

    // The URL comes from application.properties: product-service.base-url=http://localhost:8081
    // Better than writing it in Java code, but it is still ONE fixed host and port.
    public ProductClient(RestClient.Builder builder,
                         @Value("${product-service.base-url}") String productServiceUrl) {
        this.restClient = builder.baseUrl(productServiceUrl).build();
        log.info("ProductClient will call product-service at {}", productServiceUrl);
    }

    public ProductDto getProduct(Long productId) {
        try {
            return restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()                 // throws an exception for 4xx/5xx responses
                    .body(ProductDto.class);    // JSON -> ProductDto (Jackson does this)
        } catch (HttpClientErrorException.NotFound e) {
            // product-service answered "404" -> the product id is wrong. That is the caller's mistake.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + productId + " not found");
        } catch (ResourceAccessException e) {
            // We could not even CONNECT (product-service is down or on another port).
            // In a monolith this could never happen - a normal method call cannot "go offline"!
            log.error("product-service is unreachable: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "product-service is not available right now. Please try again later.");
        }
    }
}
