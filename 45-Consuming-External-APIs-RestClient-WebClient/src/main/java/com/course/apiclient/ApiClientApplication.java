package com.course.apiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/*
 * Topic 45 - Consuming External APIs
 *
 * Our app is now a CLIENT of another API (JSONPlaceholder, a free fake API with
 * posts and users). We call it in four ways and compare them:
 *
 *   1. RestTemplate      - the classic, still in many projects   (LegacyPostService)
 *   2. RestClient        - modern, recommended for normal code   (PostService)
 *   3. WebClient         - reactive / non-blocking               (ReactivePostService)
 *   4. HTTP interface    - declare a Java interface, Spring writes the code (PostHttpClient)
 *
 * Run (internet): mvn spring-boot:run
 * Run (offline) : mvn spring-boot:run -Dspring-boot.run.profiles=offline
 *                 (calls our own MockJsonPlaceholderController instead)
 *
 * @ConfigurationPropertiesScan finds our ExternalApiProperties record.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiClientApplication.class, args);
    }
}
