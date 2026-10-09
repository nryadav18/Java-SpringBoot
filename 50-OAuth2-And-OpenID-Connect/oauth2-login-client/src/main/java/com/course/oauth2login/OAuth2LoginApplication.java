package com.course.oauth2login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 50 - Part 1: OAuth2 Login ("Login with GitHub / Google")
 * ---------------------------------------------------------------
 * Our app NEVER sees the user's GitHub password. It redirects the browser to GitHub,
 * GitHub asks the user, and sends back a one-time CODE that our app swaps for a token.
 *
 *   GET /            -> public home page with "Login with ..." links
 *   GET /me          -> who logged in (attributes from GitHub/Google/Keycloak)
 *   GET /me/token    -> info about the access token our app received (not the token itself)
 *
 * Run (default profile = GitHub + Google; needs your own client id/secret as env variables):
 *   $env:GITHUB_CLIENT_ID="..."; $env:GITHUB_CLIENT_SECRET="..."; mvn spring-boot:run
 * Run against the local Keycloak from ../docker-compose.yml (no external account needed):
 *   mvn spring-boot:run "-Dspring-boot.run.profiles=keycloak"
 * Then open http://localhost:8080
 */
@SpringBootApplication
public class OAuth2LoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2LoginApplication.class, args);
    }
}
