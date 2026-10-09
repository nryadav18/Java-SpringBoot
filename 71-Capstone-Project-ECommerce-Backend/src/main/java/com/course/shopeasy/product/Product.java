package com.course.shopeasy.product;

import java.math.BigDecimal;

import com.course.shopeasy.category.Category;
import com.course.shopeasy.common.exception.InsufficientStockException;
import com.course.shopeasy.common.persistence.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Something we sell. The heart of the catalog.
 *
 * Highlights for learners:
 *  - BigDecimal for money. NEVER double: 0.1 + 0.2 = 0.30000000000000004 in double (topic 2).
 *  - @ManyToOne(fetch = LAZY): the category is loaded only when needed (default for ManyToOne is EAGER,
 *    which silently adds joins everywhere - topic 40).
 *  - @Version: optimistic locking. Every UPDATE checks "version = what I read" and increments it.
 *    If two admins edit the same product at the same time, the second save fails instead of silently
 *    overwriting the first (topic 42).
 *  - "active" flag = soft delete. Old orders still point to this product, so we never physically delete it.
 *  - Rich domain methods (decreaseStock, increaseStock) keep the stock rules INSIDE the entity, so no
 *    service can ever set a negative stock by mistake.
 */
@Entity
@Table(name = "products")
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String sku;                          // Stock Keeping Unit - the business id, e.g. "ELEC-001"

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Version
    private Long version;

    protected Product() {
    }

    public Product(String sku, String name, String description, BigDecimal price,
                   int stockQuantity, String imageUrl, Category category) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // ------------------------------------------------------------------ domain behaviour

    public void update(String name, String description, BigDecimal price, int stockQuantity,
                       String imageUrl, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    /** Takes items out of the warehouse. Refuses to go below zero. */
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (quantity > stockQuantity) {
            throw new InsufficientStockException(id, name, quantity, stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    /** Puts items back, e.g. when an order is cancelled. */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        this.stockQuantity += quantity;
    }

    public boolean hasStock(int quantity) {
        return stockQuantity >= quantity;
    }

    public void deactivate() {
        this.active = false;
    }

    // ------------------------------------------------------------------ getters

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return active; }
    public Category getCategory() { return category; }
    public Long getVersion() { return version; }
}
