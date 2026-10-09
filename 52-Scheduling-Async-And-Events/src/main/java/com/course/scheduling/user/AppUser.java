package com.course.scheduling.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * A registered user, stored with JPA (topic 38) in the H2 in-memory database.
 * We need a real database + transaction to demonstrate @TransactionalEventListener.
 */
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    protected AppUser() {
        // required by JPA
    }

    public AppUser(String name, String email) {
        this.name = name;
        this.email = email;
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
