package com.course.shopeasy.order;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.course.shopeasy.common.web.PageResponse;
import com.course.shopeasy.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * The customer's orders. Cancelling is modelled as an ACTION sub-resource (POST /orders/{id}/cancel),
 * a common REST pattern for state changes that are more than a field update (topic 44).
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Place and track my orders (CUSTOMER)")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Place an order from everything in my cart")
    public ResponseEntity<OrderResponse> placeOrder(@AuthenticationPrincipal UserPrincipal user,
                                                    @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse order = orderService.placeOrder(user.id(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(order.id()).toUri();
        return ResponseEntity.created(location).body(order);
    }

    @GetMapping
    @Operation(summary = "List my orders, newest first")
    public PageResponse<OrderResponse> myOrders(
            @AuthenticationPrincipal UserPrincipal user,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.findMyOrders(user.id(), pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one of my orders")
    public OrderResponse findOne(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long id) {
        return orderService.findOrder(id, user);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel one of my orders (only while PENDING or PAID) - stock is returned")
    public OrderResponse cancel(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long id) {
        return orderService.cancelMyOrder(id, user);
    }
}
