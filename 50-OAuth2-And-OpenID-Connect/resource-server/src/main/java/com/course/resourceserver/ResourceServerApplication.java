package com.course.resourceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 50 - Part 2: OAuth2 Resource Server
 * -----------------------------------------
 * A "Course Catalog" API that trusts access tokens issued by Keycloak (or Auth0, Okta...).
 * Compare with topic 49: there WE created and signed the tokens. Here a separate
 * Authorization Server does that - our API only CHECKS them.
 *
 *   GET /api/public/courses   -> anyone
 *   GET /api/me               -> any valid token (shows the token's claims)
 *   GET /api/profile          -> token must contain scope "profile"   (authority SCOPE_profile)
 *   GET /api/admin/report     -> token must contain realm role "admin" (authority ROLE_ADMIN)
 *
 * Run:  docker compose up -d   (in the topic folder - starts Keycloak on :8180)
 *       mvn spring-boot:run    (this API on :8081)
 */
@SpringBootApplication
public class ResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication.class, args);
    }
}
