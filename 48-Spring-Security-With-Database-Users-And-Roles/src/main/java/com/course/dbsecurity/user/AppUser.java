package com.course.dbsecurity.user;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

/*
 * AppUser - OUR user, stored in the database table "app_users".
 *
 * Why not call it "User"? Because Spring Security already has a class named User
 * (org.springframework.security.core.userdetails.User) and "user" is a reserved word
 * in some databases. "AppUser" avoids both confusions.
 *
 * Roles are stored in a second table "user_roles" (user_id, role) thanks to @ElementCollection:
 *
 *   app_users                         user_roles
 *   +----+----------+-----------+      +---------+-------+
 *   | id | username | password  |      | user_id | role  |
 *   +----+----------+-----------+      +---------+-------+
 *   | 1  | admin    | $2a$10... |      | 1       | ADMIN |
 *   | 2  | alice    | $2a$10... |      | 1       | USER  |
 *   +----+----------+-----------+      | 2       | USER  |
 *                                      +---------+-------+
 *
 * The password column holds a BCrypt HASH, never the real password.
 */
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)   // BCrypt hashes are 60 chars - leave room
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;           // an admin could "lock" a user by setting false

    // EAGER: we always need the roles when a user logs in, so load them together.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)              // store "ADMIN", not 1 - safer if the enum order changes
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    protected AppUser() {
        // required by JPA
    }

    public AppUser(String username, String passwordHash, Set<Role> roles) {
        this.username = username;
        this.password = passwordHash;
        this.roles = new HashSet<>(roles);
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isEnabled() { return enabled; }
    public Set<Role> getRoles() { return roles; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
