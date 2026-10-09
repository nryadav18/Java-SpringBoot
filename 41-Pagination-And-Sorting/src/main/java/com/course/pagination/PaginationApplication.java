package com.course.pagination;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 41 - Pagination & Sorting
 * -------------------------------
 * A product catalogue with 60 products (created by DataSeeder) that is NEVER returned all at once.
 * Clients ask for one "page" at a time, e.g.
 *
 *   GET http://localhost:8080/api/products?page=0&size=5&sort=price,desc
 *   GET http://localhost:8080/api/products/search?category=Books&maxPrice=50&page=1&size=3
 *
 * Run:  mvn spring-boot:run
 */
@SpringBootApplication
public class PaginationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaginationApplication.class, args);
    }
}
