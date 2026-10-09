package com.course.jpabasics.config;

import com.course.jpabasics.entity.Product;
import com.course.jpabasics.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/*
 * DataSeeder - puts a few sample products in the empty database at startup,
 * so GET /api/products shows something straight away.
 * A CommandLineRunner runs once, right after the application context is ready (topic 28).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ProductRepository repository;

    public DataSeeder(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;                                   // already seeded - do nothing
        }
        repository.saveAll(List.of(
                new Product("Wireless Mouse", "2.4 GHz, silent clicks", "MOU-001", new BigDecimal("19.99"), 150),
                new Product("Mechanical Keyboard", "Blue switches, RGB", "KEY-001", new BigDecimal("79.50"), 40),
                new Product("27-inch Monitor", "QHD IPS panel", "MON-027", new BigDecimal("249.00"), 12)
        ));
        log.info("Seeded {} products", repository.count());
    }
}
