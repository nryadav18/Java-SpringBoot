package com.course.shopeasy.common.exception;

/**
 * Thrown when a customer wants more units than we have in the warehouse.
 * Mapped to HTTP 409 Conflict. Because it is a RuntimeException, the surrounding
 * @Transactional order placement is rolled back: no stock is reduced, no order is saved.
 */
public class InsufficientStockException extends RuntimeException {

    private final Long productId;
    private final int requested;
    private final int available;

    public InsufficientStockException(Long productId, String productName, int requested, int available) {
        super("Not enough stock for '%s': requested %d, available %d".formatted(productName, requested, available));
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }

    public Long getProductId() { return productId; }
    public int getRequested() { return requested; }
    public int getAvailable() { return available; }
}
