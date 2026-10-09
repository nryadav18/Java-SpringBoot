package com.course.jwtauth.web;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * OrderController - protected endpoints. By the time a request arrives HERE,
 * JwtAuthenticationFilter has already put the user into the SecurityContext,
 * so @AuthenticationPrincipal / Authentication work exactly like in topic 48.
 *
 *   curl http://localhost:8080/api/hello -H "Authorization: Bearer <token>"
 */
@RestController
@RequestMapping("/api")
public class OrderController {

    public record OrderSummary(int orderId, String restaurant, double total, String status) { }

    @GetMapping("/hello")
    public Map<String, Object> hello(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Map.of("message", "Hello " + authentication.getName() + ", your token is valid!", "roles", roles);
    }

    /** Fake data - the point is WHO is asking, not the orders themselves. */
    @GetMapping("/orders")
    public Map<String, Object> myOrders(@AuthenticationPrincipal UserDetails me) {
        List<OrderSummary> orders = List.of(
                new OrderSummary(101, "Pizza Palace", 18.50, "DELIVERED"),
                new OrderSummary(102, "Curry House", 12.00, "ON_THE_WAY"));
        return Map.of("customer", me.getUsername(), "orders", orders);
    }

    /** URL rule hasRole("ADMIN") already protects /api/admin/** - @PreAuthorize is a 2nd lock. */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> dashboard() {
        return Map.of("ordersToday", 342, "activeRiders", 57, "revenueToday", 8123.75);
    }
}
