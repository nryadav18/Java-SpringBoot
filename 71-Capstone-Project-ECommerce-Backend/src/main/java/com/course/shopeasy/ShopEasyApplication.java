package com.course.shopeasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * ShopEasy - the course capstone: an e-commerce REST backend.
 *
 * What this file demonstrates:
 *   - The single entry point of a Spring Boot app (topic 28).
 *   - @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan.
 *     Component scanning starts at THIS package (com.course.shopeasy), so every feature package
 *     below it (auth, user, product, category, cart, order, ...) is found automatically.
 *   - @ConfigurationPropertiesScan finds our type-safe settings records
 *     (JwtProperties, SeedProperties) annotated with @ConfigurationProperties (topic 29).
 *
 * How to run:
 *   mvn spring-boot:run                       (H2 in-memory DB, port 8080)
 *   mvn spring-boot:run -Dspring-boot.run.profiles=mysql   (needs: docker compose up -d mysql)
 * Then open http://localhost:8080/swagger-ui.html
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ShopEasyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopEasyApplication.class, args);
    }
}
