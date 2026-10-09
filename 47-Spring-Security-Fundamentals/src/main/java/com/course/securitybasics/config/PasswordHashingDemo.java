package com.course.securitybasics.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/*
 * PasswordHashingDemo - runs once at startup and prints what BCrypt does.
 *
 * Look at the console when the app starts. You will see that:
 *   - the same password hashed TWICE gives TWO DIFFERENT hashes (random salt!)
 *   - matches(...) still says true for both
 *   - a wrong password gives false
 *
 * Example output (your hashes are different on every run):
 *   hash #1 = $2a$10$Qm3x....................................  (60 characters)
 *   hash #2 = $2a$10$z8Lk....................................
 *   Same password, different hashes? true
 *   matches("student123", hash #1) = true
 *   matches("student123", hash #2) = true
 *   matches("wrong",      hash #1) = false
 */
@Component
public class PasswordHashingDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordHashingDemo.class);

    private final PasswordEncoder passwordEncoder;

    public PasswordHashingDemo(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String hash1 = passwordEncoder.encode("student123");
        String hash2 = passwordEncoder.encode("student123");

        log.info("---- BCrypt demo ----");
        log.info("hash #1 = {}", hash1);
        log.info("hash #2 = {}", hash2);
        log.info("Same password, different hashes? {}", !hash1.equals(hash2));
        log.info("matches(\"student123\", hash #1) = {}", passwordEncoder.matches("student123", hash1));
        log.info("matches(\"student123\", hash #2) = {}", passwordEncoder.matches("student123", hash2));
        log.info("matches(\"wrong\",      hash #1) = {}", passwordEncoder.matches("wrong", hash1));
    }
}
