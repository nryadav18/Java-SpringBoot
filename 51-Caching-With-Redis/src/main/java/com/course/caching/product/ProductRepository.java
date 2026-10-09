package com.course.caching.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/*
 * A FAKE, deliberately SLOW "database".
 *
 * Every read waits app.slow-database.delay-ms milliseconds (2000 by default) to simulate
 * a slow SQL query or a slow remote service. This makes the benefit of caching easy to SEE.
 *
 * It also counts how many times it was really called ("database hits"), so you can
 * prove that a cache HIT never reaches this class.
 */
@Repository
public class ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);

    private final Map<Long, Product> table = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();
    private final AtomicInteger databaseHits = new AtomicInteger();
    private final long delayMs;

    public ProductRepository(@Value("${app.slow-database.delay-ms:2000}") long delayMs) {
        this.delayMs = delayMs;
        // some sample data
        save(new Product(null, "Dell XPS 13", "laptops", new BigDecimal("1199.00")));
        save(new Product(null, "MacBook Air M3", "laptops", new BigDecimal("1299.00")));
        save(new Product(null, "Logitech MX Master 3S", "accessories", new BigDecimal("99.99")));
        save(new Product(null, "Samsung Galaxy S24", "phones", new BigDecimal("899.00")));
        save(new Product(null, "Pixel 9", "phones", new BigDecimal("799.00")));
    }

    public Optional<Product> findById(Long id) {
        simulateSlowQuery("SELECT * FROM product WHERE id = " + id);
        return Optional.ofNullable(table.get(id));
    }

    public List<Product> findByCategory(String category) {
        simulateSlowQuery("SELECT * FROM product WHERE category = '" + category + "'");
        // IMPORTANT: return a normal ArrayList, NOT stream().toList() or List.of().
        // Java's hidden immutable list classes cannot be read back from Redis JSON (see lesson, "mistakes").
        List<Product> result = new ArrayList<>();
        table.values().stream()
                .filter(p -> p.category().equalsIgnoreCase(category))
                .sorted(Comparator.comparing(Product::id))
                .forEach(result::add);
        return result;
    }

    public Product save(Product product) {
        Product toSave = product.id() == null
                ? new Product(idSequence.incrementAndGet(), product.name(), product.category(), product.price())
                : product;
        table.put(toSave.id(), toSave);
        return toSave;
    }

    public boolean deleteById(Long id) {
        return table.remove(id) != null;
    }

    public int getDatabaseHits() {
        return databaseHits.get();
    }

    private void simulateSlowQuery(String sql) {
        int hit = databaseHits.incrementAndGet();
        log.info("DATABASE HIT #{} -> {} (takes {} ms)", hit, sql, delayMs);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // keep the "interrupted" flag (good practice)
        }
    }
}
