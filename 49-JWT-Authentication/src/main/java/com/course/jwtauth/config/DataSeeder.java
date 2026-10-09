package com.course.jwtauth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.course.jwtauth.user.AppUser;
import com.course.jwtauth.user.AppUserRepository;
import com.course.jwtauth.user.Role;

/*
 * DataSeeder - creates admin/admin123 (ADMIN) and alice/alice123 (USER) if they are missing.
 * Demo only: in real life, read the first admin's password from an environment variable.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seed("admin", "admin123", Role.ADMIN);
        seed("alice", "alice123", Role.USER);
    }

    private void seed(String username, String rawPassword, Role role) {
        if (!users.existsByUsername(username)) {
            users.save(new AppUser(username, passwordEncoder.encode(rawPassword), role));
            log.info("Seeded user '{}' with role {}", username, role);
        }
    }
}
