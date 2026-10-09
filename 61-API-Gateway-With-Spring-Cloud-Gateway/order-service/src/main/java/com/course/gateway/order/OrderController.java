/*
 * OrderController.java  (Topic 61 - order-service)
 *
 * WHAT: Places and lists orders. Notice the X-Request-Source header: it is set by the
 *       gateway's AddRequestHeader filter, so you can see whether the request came
 *       through the gateway or directly.
 *
 * TRY (through the gateway - POST needs the API key, see ApiKeyGlobalFilter):
 *       POST http://localhost:8080/api/orders
 *            X-API-KEY: course-secret-123
 *            {"productId":1,"quantity":2}
 *       GET  http://localhost:8080/api/orders
 */
package com.course.gateway.order;

import com.course.gateway.order.OrderModels.Order;
import com.course.gateway.order.OrderModels.OrderRequest;
import com.course.gateway.order.OrderModels.ProductDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final RestClient productRestClient;
    private final List<Order> orders = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public OrderController(RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(@RequestBody OrderRequest request,
                            @RequestHeader(value = "X-Request-Source", defaultValue = "direct") String source) {
        if (request.productId() == null || request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required and quantity must be at least 1");
        }
        ProductDto product;
        try {
            product = productRestClient.get()
                    .uri("/api/products/{id}", request.productId())
                    .retrieve()
                    .body(ProductDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + request.productId() + " not found");
        }
        Order order = new Order(nextId.getAndIncrement(), product.id(), product.name(), request.quantity(),
                product.price() * request.quantity(), product.servedBy(), source);
        orders.add(order);
        return order;
    }

    @GetMapping
    public List<Order> findAll() {
        return orders;
    }
}
