/*
 * ProductClient.java  (Topic 60 - order-service)
 *
 * WHAT: Calls product-service using the load-balanced RestClient from RestClientConfig.
 *       Compare with Topic 59: there is NO "localhost:8081" anywhere in this project!
 */
package com.course.discovery.order;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductClient {

    private final RestClient productRestClient;

    public ProductClient(RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    public ProductDto getProduct(Long productId) {
        try {
            return productRestClient.get()
                    .uri("/api/products/{id}", productId)   // -> http://product-service/api/products/1
                    .retrieve()
                    .body(ProductDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            // product-service is alive but says "no such product"
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + productId + " not found");
        } catch (HttpStatusCodeException e) {
            // any other 4xx/5xx answer (the load balancer itself answers 503 when
            // Eureka knows NO live instance of product-service)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "product-service answered " + e.getStatusCode() + " - is at least one instance running?");
        } catch (ResourceAccessException | IllegalStateException e) {
            // Could not connect (instance just died) or no instance found in the registry
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No reachable product-service instance: " + e.getMessage());
        }
    }
}
