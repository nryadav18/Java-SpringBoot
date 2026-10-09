package com.course.graphql;

/*
 * Topic 56 - GraphQL with Spring
 * ------------------------------
 * A tiny library API (Books & Authors) exposed with GraphQL instead of REST.
 *
 * Run:   mvn spring-boot:run
 * Open:  http://localhost:8080/graphiql   (in-browser query editor)
 * Every query goes to ONE endpoint:  POST http://localhost:8080/graphql
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphqlLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlLibraryApplication.class, args);
    }
}
