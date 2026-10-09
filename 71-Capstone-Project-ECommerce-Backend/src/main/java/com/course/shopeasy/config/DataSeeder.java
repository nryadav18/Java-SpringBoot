package com.course.shopeasy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.course.shopeasy.user.Role;
import com.course.shopeasy.user.User;
import com.course.shopeasy.user.UserRepository;

/**
 * Creates the two demo users when the application starts (topic 28: ApplicationRunner / CommandLineRunner).
 *
 *   admin@shopeasy.com    / Admin@123     (role ADMIN)
 *   customer@shopeasy.com / Customer@123  (role CUSTOMER)
 *
 * Why here and not in a Flyway SQL file like the products?  Passwords must be stored as BCrypt hashes,
 * and the cleanest way to produce one is the same PasswordEncoder bean the app uses for login.
 * The seeder is idempotent: it only inserts a user if the e-mail does not exist yet, so restarting
 * the app against MySQL does not create duplicates.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    public static final String ADMIN_EMAIL = "admin@shopeasy.com";
    public static final String ADMIN_PASSWORD = "Admin@123";
    public static final String CUSTOMER_EMAIL = "customer@shopeasy.com";
    public static final String CUSTOMER_PASSWORD = "Customer@123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedProperties seedProperties;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, SeedProperties seedProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedProperties = seedProperties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedProperties.enabled()) {
            log.info("User seeding disabled (shopeasy.seed.enabled=false)");
            return;
        }
        createIfMissing("ShopEasy Admin", ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        createIfMissing("Demo Customer", CUSTOMER_EMAIL, CUSTOMER_PASSWORD, Role.CUSTOMER);
    }

    private void createIfMissing(String name, String email, String rawPassword, Role role) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        userRepository.save(new User(name, email, passwordEncoder.encode(rawPassword), role));
        // Log the e-mail, NEVER the password (topic 30: what not to log).
        log.info("Seeded demo user {} with role {}", email, role);
    }
}
