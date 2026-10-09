/*
 * OrderModels.java  (Topic 61 - order-service)
 *
 * WHAT: The three small records this minimal service needs, kept in ONE file to keep the
 *       project short. (A file can contain several top-level types if only one is public;
 *       here the holder class is public and the records are nested inside it.)
 *
 *   OrderModels.OrderRequest  - JSON in:   {"productId":1,"quantity":2}
 *   OrderModels.ProductDto    - JSON from product-service
 *   OrderModels.Order         - JSON out
 */
package com.course.gateway.order;

public final class OrderModels {

    private OrderModels() {
        // only a holder for the records below - never instantiated
    }

    public record OrderRequest(Long productId, int quantity) {
    }

    public record ProductDto(Long id, String name, double price, int stock, String servedBy) {
    }

    public record Order(Long orderId, Long productId, String productName, int quantity,
                        double totalPrice, String productServedBy, String requestSource) {
    }
}
