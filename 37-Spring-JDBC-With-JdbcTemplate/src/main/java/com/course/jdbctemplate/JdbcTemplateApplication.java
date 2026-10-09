package com.course.jdbctemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 37 - Spring JDBC with JdbcTemplate
 * ----------------------------------------
 * A Student REST API backed by a real SQL database (H2 in memory).
 *
 * At startup Spring Boot:
 *   1. creates a HikariCP connection pool (DataSource) for jdbc:h2:mem:studentdb
 *   2. runs schema.sql (CREATE TABLE) and data.sql (INSERT sample rows)
 *   3. creates JdbcTemplate, NamedParameterJdbcTemplate and JdbcClient beans
 *
 * Run:   mvn spring-boot:run
 * API:   http://localhost:8080/api/students
 * DB UI: http://localhost:8080/h2-console   (JDBC URL: jdbc:h2:mem:studentdb, user: sa, no password)
 */
@SpringBootApplication
public class JdbcTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdbcTemplateApplication.class, args);
    }
}
