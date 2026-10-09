package com.course.jparelationships;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 40 - JPA Relationships
 * ----------------------------
 * A tiny online shop with four linked entities:
 *
 *     Customer 1 ---- 1 Address          (@OneToOne)
 *     Customer 1 ---- * PurchaseOrder    (@OneToMany / @ManyToOne, bidirectional)
 *     PurchaseOrder * ---- * Product     (@ManyToMany with a join table)
 *
 * DataSeeder creates sample data at startup. Then try:
 *   GET http://localhost:8080/api/customers
 *   GET http://localhost:8080/api/customers/1
 *   GET http://localhost:8080/api/customers/demo/n-plus-one     <- watch the console!
 *   GET http://localhost:8080/api/customers/demo/join-fetch
 *
 * Run:  mvn spring-boot:run
 */
@SpringBootApplication
public class JpaRelationshipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaRelationshipsApplication.class, args);
    }
}
