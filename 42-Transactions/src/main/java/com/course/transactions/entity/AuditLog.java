package com.course.transactions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/*
 * AuditLog - "who tried to do what, when".
 * Banks must keep a record of every ATTEMPT, even the failed ones. That's why
 * AuditService writes these rows in their OWN transaction (Propagation.REQUIRES_NEW):
 * if the transfer rolls back, the audit row stays.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String message;

    /** How the row was written: "REQUIRES_NEW" or "REQUIRED" - so you can see which ones survived. */
    @Column(nullable = false, length = 20)
    private String propagation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AuditLog() {
    }

    public AuditLog(String message, String propagation) {
        this.message = message;
        this.propagation = propagation;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getPropagation() { return propagation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
