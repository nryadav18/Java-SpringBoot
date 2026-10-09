package com.course.shopeasy.common.persistence;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Common "when was this row created / last changed?" columns (topic 38: auditing fields).
 *
 * @MappedSuperclass means: this is NOT a table on its own. Its fields are copied into the table of every
 * entity that extends it (users, categories, products, carts, orders).
 * Hibernate fills the values automatically - we never set them by hand.
 */
@MappedSuperclass
public abstract class AuditableEntity {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
