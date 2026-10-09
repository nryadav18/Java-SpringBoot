package com.course.jparelationships.controller;

import com.course.jparelationships.dto.Dtos.CreateCustomerRequest;
import com.course.jparelationships.dto.Dtos.CreateOrderRequest;
import com.course.jparelationships.dto.Dtos.CustomerDetails;
import com.course.jparelationships.dto.Dtos.CustomerSummary;
import com.course.jparelationships.dto.Dtos.OrderResponse;
import com.course.jparelationships.dto.Dtos.QueryStats;
import com.course.jparelationships.entity.Customer;
import com.course.jparelationships.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
 * CustomerController
 *   GET    /api/customers                         list (summary DTOs)
 *   GET    /api/customers/{id}                    details with address, orders, products (DTOs)
 *   GET    /api/customers/{id}/raw                the ENTITY itself (Jackson annotations demo)
 *   POST   /api/customers                         create customer + address (cascade)
 *   POST   /api/customers/{id}/orders             place an order with product ids (many-to-many)
 *   DELETE /api/customers/{id}/orders/{orderId}   remove order (orphanRemoval)
 *   DELETE /api/customers/{id}                    delete customer (cascade remove)
 *   GET    /api/customers/demo/n-plus-one         count SQL statements: N+1 problem
 *   GET    /api/customers/demo/join-fetch         count SQL statements: JOIN FETCH fix
 *   GET    /api/customers/demo/entity-graph       count SQL statements: @EntityGraph fix
 *   GET    /api/customers/demo/lazy-error/{id}    LazyInitializationException on purpose
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<CustomerSummary> list() {
        return service.listCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDetails details(@PathVariable Long id) {
        return service.getDetails(id);
    }

    @GetMapping("/{id}/raw")
    public Customer raw(@PathVariable Long id) {
        return service.getRawEntity(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDetails create(@Valid @RequestBody CreateCustomerRequest request) {
        return service.create(request);
    }

    @PostMapping("/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@PathVariable Long id, @Valid @RequestBody CreateOrderRequest request) {
        return service.placeOrder(id, request);
    }

    @DeleteMapping("/{id}/orders/{orderId}")
    public ResponseEntity<Void> removeOrder(@PathVariable Long id, @PathVariable Long orderId) {
        service.removeOrder(id, orderId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- demos ----------------
    @GetMapping("/demo/n-plus-one")
    public QueryStats nPlusOne() {
        return service.demoNPlusOne();
    }

    @GetMapping("/demo/join-fetch")
    public QueryStats joinFetch() {
        return service.demoJoinFetch();
    }

    @GetMapping("/demo/entity-graph")
    public QueryStats entityGraph() {
        return service.demoEntityGraph();
    }

    @GetMapping("/demo/lazy-error/{id}")
    public Map<String, String> lazyError(@PathVariable Long id) {
        return service.demoLazyInitializationException(id);
    }
}
