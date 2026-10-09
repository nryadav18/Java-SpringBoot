package com.course.jpabasics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Product - our first JPA ENTITY.
 * --------------------------------
 * An entity is a normal Java class that JPA "maps" to a database table:
 *     one class   <->  one table      (products)
 *     one field   <->  one column     (name, price ...)
 *     one object  <->  one row
 *
 * Hibernate reads these annotations and generates the CREATE TABLE statement for us
 * (because ddl-auto=create-drop in application.properties).
 *
 * Note: we use a normal class, NOT a record, because JPA needs:
 *   - a no-argument constructor (Hibernate creates the object first, then fills the fields),
 *   - mutable fields (Hibernate changes them when loading / we change them when updating).
 */
@Entity                         // "Hey JPA, please manage this class and store it in a table."
@Table(name = "products")       // Optional: table name. Without it the table would be called "product".
public class Product {

    @Id                                                   // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // the DATABASE generates 1, 2, 3 ... (auto-increment)
    private Long id;                                      // Long (not long) so "no id yet" can be null

    @Column(nullable = false, length = 100)               // NOT NULL, VARCHAR(100)
    private String name;

    @Column(length = 500)                                 // VARCHAR(500), may be null
    private String description;

    @Column(nullable = false, unique = true, length = 30) // every SKU (stock-keeping unit) must be unique
    private String sku;

    @Column(nullable = false, precision = 10, scale = 2)  // DECIMAL(10,2) - never use double for money!
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)    // field "stock" -> column "stock_quantity"
    private int stock;

    // ---------- automatic audit fields ----------
    @CreationTimestamp                                    // Hibernate fills this ONCE when the row is inserted
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp                                      // Hibernate refreshes this on EVERY update
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Required by JPA. "protected" so our own code is encouraged to use the other constructor. */
    protected Product() {
    }

    public Product(String name, String description, String sku, BigDecimal price, int stock) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
    }

    // ---------- getters & setters (no setter for id / createdAt: those are managed for us) ----------
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', sku='" + sku + "', price=" + price + ", stock=" + stock + "}";
    }
}
