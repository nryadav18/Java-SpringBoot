package com.course.pagination.config;

import com.course.pagination.entity.Product;
import com.course.pagination.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * DataSeeder - creates 60 predictable products (12 per category) so paging has something to page.
 * Prices, stock and dates are calculated from the index, so every run gives the same data.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    static final String[] CATEGORIES = {"Books", "Electronics", "Fashion", "Home", "Sports"};
    private static final String[][] NAMES = {
            {"Classic Novel", "Cookbook", "Travel Guide", "Comic", "Poetry Collection", "Biography",
             "Science Fiction", "Children's Book", "History Book", "Self-Help", "Dictionary", "Atlas"},
            {"Smartphone", "Laptop", "Headphones", "Smartwatch", "Tablet", "Camera",
             "Speaker", "Keyboard", "Mouse", "Monitor", "Charger", "Power Bank"},
            {"T-Shirt", "Jeans", "Jacket", "Sneakers", "Dress", "Scarf",
             "Cap", "Sweater", "Shorts", "Belt", "Socks", "Sunglasses"},
            {"Lamp", "Cushion", "Blender", "Toaster", "Rug", "Mirror",
             "Kettle", "Clock", "Vase", "Curtains", "Pan", "Plant Pot"},
            {"Football", "Yoga Mat", "Tennis Racket", "Dumbbells", "Bicycle Helmet", "Running Shoes",
             "Water Bottle", "Jump Rope", "Gym Bag", "Swim Goggles", "Basketball", "Skateboard"}
    };

    private final ProductRepository repository;

    public DataSeeder(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(sampleProducts());
        log.info("Seeded {} products", repository.count());
    }

    /** Public + static so tests can reuse exactly the same data. */
    public static List<Product> sampleProducts() {
        List<Product> products = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 9, 0);
        for (int i = 0; i < 60; i++) {
            int cat = i % CATEGORIES.length;          // 0,1,2,3,4,0,1,...
            int n = i / CATEGORIES.length;            // 0..11
            BigDecimal price = BigDecimal.valueOf(5 + (i * 37 % 200)).add(new BigDecimal("0.99"));
            int stock = (i * 13) % 50;                // 0..49, some products are low in stock
            products.add(new Product(NAMES[cat][n], CATEGORIES[cat], price, stock, start.plusDays(i)));
        }
        return products;
    }
}
