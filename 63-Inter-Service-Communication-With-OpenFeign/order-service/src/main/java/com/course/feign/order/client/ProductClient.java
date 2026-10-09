/*
 * ProductClient - a DECLARATIVE HTTP client.
 * ---------------------------------------------------------------
 * There is NO implementation class anywhere! At startup OpenFeign reads the annotations
 * below and builds a proxy object that:
 *   1. turns each method call into an HTTP request,
 *   2. sends it to product-service,
 *   3. converts the JSON answer back into Java objects (with Jackson),
 *   4. passes any 4xx/5xx answer to our ErrorDecoder (ProductClientErrorDecoder).
 *
 * name = "product-service"
 *   - the logical name of the remote service. It is also the key for per-client settings
 *     in application.yml (spring.cloud.openfeign.client.config.product-service.*).
 *   - When url is EMPTY, Feign asks Spring Cloud LoadBalancer (+ Eureka) to turn
 *     "product-service" into a real host:port -> client-side load balancing.
 *
 * url = "${clients.product-service.url:}"
 *   - read from application.yml. Default profile: http://localhost:8081 (works without Eureka).
 *   - the "eureka" profile sets it to "" so the name above is used for discovery.
 *
 * The annotations are the SAME Spring MVC annotations you use in controllers
 * (@GetMapping, @PathVariable, @RequestParam...) - that is the beauty of Spring Cloud OpenFeign.
 * Tip: always give @PathVariable / @RequestParam an explicit name ("id") in Feign interfaces.
 */
package com.course.feign.order.client;

import com.course.feign.order.config.ProductClientConfig;
import com.course.feign.order.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "product-service",
        url = "${clients.product-service.url:}",
        path = "/api/products",                     // common prefix for every method below
        configuration = ProductClientConfig.class)  // error decoder + interceptor for THIS client only
public interface ProductClient {

    // -> GET http://localhost:8081/api/products
    @GetMapping
    List<ProductResponse> getAllProducts();

    // -> GET http://localhost:8081/api/products/1
    @GetMapping("/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);

    // -> GET http://localhost:8081/api/products/1?delayMs=3000  (used to demo the read timeout)
    @GetMapping("/{id}")
    ProductResponse getProductSlowly(@PathVariable("id") Long id, @RequestParam("delayMs") long delayMs);

    // -> POST http://localhost:8081/api/products/1/reduce-stock?quantity=2
    @PostMapping("/{id}/reduce-stock")
    ProductResponse reduceStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);
}
