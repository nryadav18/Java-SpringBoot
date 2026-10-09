package com.course.jparelationships.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/*
 * Product - the INVERSE side of the many-to-many with PurchaseOrder.
 * ------------------------------------------------------------------
 * mappedBy = "products" means: "the join table is described by the field 'products'
 * in PurchaseOrder - look there". Changing only THIS set would not be saved.
 *
 * @JsonIgnore: when the /raw endpoint serializes an order's products, Jackson must not
 * follow product.orders back to the orders (another infinite loop). We simply skip it.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToMany(mappedBy = "products")     // LAZY by default
    @JsonIgnore
    private Set<PurchaseOrder> orders = new HashSet<>();

    protected Product() {
    }

    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Set<PurchaseOrder> getOrders() { return orders; }
}
