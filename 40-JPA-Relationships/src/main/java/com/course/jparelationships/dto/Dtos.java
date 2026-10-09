package com.course.jparelationships.dto;

import com.course.jparelationships.entity.Address;
import com.course.jparelationships.entity.Customer;
import com.course.jparelationships.entity.Product;
import com.course.jparelationships.entity.PurchaseOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/*
 * All DTOs of this topic, grouped in one file as nested records to keep the folder small.
 * (In a real project, one file per record is more common.)
 *
 * DTOs are THE best fix for the JSON infinite recursion problem:
 * they are flat, they contain only what we choose, and they never point "back" to a parent.
 *
 * Every from(...) method must be called INSIDE a transaction, because it touches lazy relationships.
 */
public final class Dtos {

    private Dtos() {
    }

    // ---------------- requests ----------------
    public record AddressDto(@NotBlank String street, @NotBlank String city, String zipCode,
                             @NotBlank String country) {
        public static AddressDto from(Address a) {
            return a == null ? null : new AddressDto(a.getStreet(), a.getCity(), a.getZipCode(), a.getCountry());
        }

        public Address toEntity() {
            return new Address(street, city, zipCode, country);
        }
    }

    public record CreateCustomerRequest(@NotBlank String name, @NotBlank @Email String email,
                                        @NotNull @Valid AddressDto address) {
    }

    public record CreateOrderRequest(@NotEmpty List<Long> productIds) {
    }

    // ---------------- responses ----------------
    public record ProductResponse(Long id, String name, BigDecimal price) {
        public static ProductResponse from(Product p) {
            return new ProductResponse(p.getId(), p.getName(), p.getPrice());
        }
    }

    public record OrderResponse(Long id, String orderNumber, LocalDateTime orderDate,
                                List<ProductResponse> products, BigDecimal total) {
        public static OrderResponse from(PurchaseOrder o) {
            return new OrderResponse(o.getId(), o.getOrderNumber(), o.getOrderDate(),
                    o.getProducts().stream().map(ProductResponse::from).toList(), o.getTotal());
        }
    }

    /** Small shape for lists: no nested collections, just a count. */
    public record CustomerSummary(Long id, String name, String email, String city, int orderCount) {
        public static CustomerSummary from(Customer c) {
            return new CustomerSummary(c.getId(), c.getName(), c.getEmail(),
                    c.getAddress() == null ? null : c.getAddress().getCity(), c.getOrders().size());
        }
    }

    /** Full shape for the details screen. */
    public record CustomerDetails(Long id, String name, String email, AddressDto address,
                                  List<OrderResponse> orders) {
        public static CustomerDetails from(Customer c) {
            return new CustomerDetails(c.getId(), c.getName(), c.getEmail(), AddressDto.from(c.getAddress()),
                    c.getOrders().stream().map(OrderResponse::from).toList());
        }
    }

    /** Result of the N+1 demo endpoints. */
    public record QueryStats(String approach, int customers, long sqlStatements, List<CustomerSummary> data) {
    }
}
