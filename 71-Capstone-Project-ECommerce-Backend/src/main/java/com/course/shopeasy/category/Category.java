package com.course.shopeasy.category;

import com.course.shopeasy.common.persistence.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A product category such as "Electronics" or "Books".
 *
 * The relationship is mapped only on the Product side (Product -> Category, @ManyToOne).
 * We do NOT add a List of products here: a category may have thousands of products, and a
 * one-directional mapping is simpler and avoids accidental huge loads (topic 40).
 */
@Entity
@Table(name = "categories")
public class Category extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 255)
    private String description;

    protected Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /** Small "domain method" instead of two public setters - the entity decides what can change. */
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
