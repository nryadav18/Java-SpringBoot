package com.course.pagination.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Product - a simple catalogue item.
 * The @Index annotations ask Hibernate to create database indexes on the columns we
 * filter and sort by most. Sorting a big table WITHOUT an index is slow (the DB must sort every row).
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_category", columnList = "category"),
        @Index(name = "idx_products_price", columnList = "price")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 40)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private int stock;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Product() {
    }

    public Product(String name, String category, BigDecimal price, int stock, LocalDateTime createdAt) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
