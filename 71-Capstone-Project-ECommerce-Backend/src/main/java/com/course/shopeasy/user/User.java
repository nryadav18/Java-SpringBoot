package com.course.shopeasy.user;

import com.course.shopeasy.common.persistence.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A registered person - admin or customer (table "users"; "user" is a reserved word in many databases).
 *
 * Design notes:
 *   - No Lombok on purpose: plain getters keep the code readable for learners (topic 33).
 *   - No public setters for id/email: an entity should protect its own rules. Only what may change is changeable.
 *   - The password field holds a BCrypt HASH, never the real password (topic 47).
 *   - The protected no-args constructor exists only because JPA needs it to create objects from rows.
 */
@Entity
@Table(name = "users")
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // the DB's AUTO_INCREMENT generates the id
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    protected User() {
        // for JPA only
    }

    public User(String fullName, String email, String passwordHash, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.password = passwordHash;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public boolean isEnabled() { return enabled; }

    public void disable() { this.enabled = false; }
}
