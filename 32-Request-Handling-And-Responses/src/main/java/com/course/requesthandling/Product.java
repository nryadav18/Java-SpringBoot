package com.course.requesthandling;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * The Product resource, plus a tour of the most useful Jackson annotations:
 *
 *   @JsonProperty("stock")  -> the Java field "quantityInStock" appears in JSON as "stock"
 *   @JsonIgnore             -> "supplierCode" is internal; it is NEVER written to (or read from) JSON
 *   @JsonFormat(pattern=..) -> dates are written as "09-10-2026 14:30:00" instead of the ISO default
 *
 * Example JSON produced for a Product:
 *   {"id":1,"name":"Wireless Mouse","category":"electronics","price":799.00,
 *    "stock":50,"createdAt":"09-10-2026 14:30:00","updatedAt":"09-10-2026 14:30:00"}
 *
 * This is a normal class (not a record) because PATCH changes single fields.
 * In topic 33 we separate the "entity" from the JSON "DTO" properly.
 * BigDecimal is used for money because double has rounding errors (0.1 + 0.2 != 0.3).
 */
public class Product {

    private Long id;
    private String name;
    private String category;
    private BigDecimal price;

    @JsonProperty("stock")
    private int quantityInStock;

    @JsonIgnore
    private String supplierCode;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    public Product() {
        // Jackson needs a no-argument constructor to create objects from JSON.
    }

    public Product(Long id, String name, String category, BigDecimal price, int quantityInStock, String supplierCode) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.supplierCode = supplierCode;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // ---- getters and setters (Jackson uses the getters to build JSON) ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
