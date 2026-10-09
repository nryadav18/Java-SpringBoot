/*
 * Topic 64 - Resilience4j | inventory-service (the "unreliable" downstream)
 * ---------------------------------------------------------------
 * A tiny stock service. Its only special feature is a CHAOS SWITCH that makes it
 * fail (HTTP 500) or answer slowly, so we can test how store-service protects itself.
 *
 * Run:  cd inventory-service  ->  mvn spring-boot:run     (http://localhost:8083)
 */
package com.course.resilience.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
