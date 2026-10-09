package com.course.jpabasics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 38 - Spring Data JPA Fundamentals
 * ---------------------------------------
 * Entry point of the "Product Catalog" REST API.
 *
 * Because spring-boot-starter-data-jpa + H2 are on the classpath, Spring Boot auto-configures:
 *   1. a DataSource (connection pool HikariCP) pointing at the H2 in-memory database,
 *   2. an EntityManagerFactory (Hibernate) that scans our @Entity classes,
 *   3. a transaction manager,
 *   4. an implementation class for every interface that extends JpaRepository.
 * We write ZERO of that plumbing ourselves.
 *
 * Run:  mvn spring-boot:run    then open http://localhost:8080/api/products
 */
@SpringBootApplication
public class JpaBasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaBasicsApplication.class, args);
    }
}
