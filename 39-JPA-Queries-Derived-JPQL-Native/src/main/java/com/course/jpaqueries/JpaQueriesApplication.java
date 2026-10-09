package com.course.jpaqueries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 39 - JPA Queries: derived, JPQL, native
 * ---------------------------------------------
 * A small "Library search" API. 13 books are loaded from src/main/resources/data.sql.
 * Every endpoint in BookController uses a DIFFERENT way of querying the database,
 * so you can compare them side by side (and watch the generated SQL in the console).
 *
 * Run:  mvn spring-boot:run    then try  http://localhost:8080/api/books
 */
@SpringBootApplication
public class JpaQueriesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaQueriesApplication.class, args);
    }
}
