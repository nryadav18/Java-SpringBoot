package com.course.shopeasy.order;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.course.shopeasy.common.web.PageResponse;
import com.course.shopeasy.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Back-office endpoints for the shop staff. Separate URL prefix (/api/v1/admin/**) so one security rule
 * protects all of them, plus @PreAuthorize on the class as a second check.
 */
@RestController
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Orders", description = "Manage all orders (ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "List all orders, optionally filtered by status")
    public PageResponse<OrderResponse> findAll(
            @RequestParam(required = false) OrderStatus status,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.findAll(status, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get any order")
    public OrderResponse findOne(@AuthenticationPrincipal UserPrincipal admin, @PathVariable Long id) {
        return orderService.findOrder(id, admin);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Move an order to a new status (PAID, SHIPPED, DELIVERED, CANCELLED)")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }
}
