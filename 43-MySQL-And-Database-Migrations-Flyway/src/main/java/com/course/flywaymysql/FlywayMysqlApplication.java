package com.course.flywaymysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 43 - MySQL and Database Migrations with Flyway
 *
 * What this app demonstrates:
 *   1. The SAME code runs on H2 (default) or MySQL (profile "mysql").
 *      Only configuration changes - not a single line of Java.
 *   2. Hibernate does NOT create the tables (ddl-auto=validate).
 *      Flyway creates and evolves them with versioned SQL scripts:
 *          db/migration/V1__create_books_table.sql
 *          db/migration/V2__insert_sample_books.sql
 *          db/migration/V3__add_isbn_column.sql
 *
 * How to run:
 *   H2    : mvn spring-boot:run
 *   MySQL : docker compose up -d
 *           mvn spring-boot:run -Dspring-boot.run.profiles=mysql
 *
 * Then open http://localhost:8080/api/db-info  and  http://localhost:8080/api/books
 */
@SpringBootApplication
public class FlywayMysqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlywayMysqlApplication.class, args);
    }
}
