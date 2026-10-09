package com.course.shopeasy.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA writes the implementation of this interface for us at startup (topic 38).
 * The two extra methods are "derived queries" (topic 39): Spring reads the method NAME and builds the SQL.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** SELECT * FROM users WHERE lower(email) = lower(?) */
    Optional<User> findByEmailIgnoreCase(String email);

    /** SELECT COUNT(*) > 0 FROM users WHERE lower(email) = lower(?) */
    boolean existsByEmailIgnoreCase(String email);
}
