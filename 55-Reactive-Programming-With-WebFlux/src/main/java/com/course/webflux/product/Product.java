package com.course.webflux.product;

/*
 * Product entity for Spring Data R2DBC.
 *
 * Notice: NO @Entity here! R2DBC is not JPA/Hibernate.
 *  - @Table says which table this class maps to.
 *  - @Id marks the primary key. When id is null, save() does an INSERT;
 *    when id has a value, save() does an UPDATE.
 * R2DBC has no lazy loading and no relationships (@OneToMany) - it is simpler.
 */
import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("products")
public class Product {

    @Id
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;

    // Spring Data needs a no-args constructor to create objects from rows.
    public Product() {
    }

    public Product(Long id, String name, String category, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name=" + name + ", category=" + category + ", price=" + price + "}";
    }
}
