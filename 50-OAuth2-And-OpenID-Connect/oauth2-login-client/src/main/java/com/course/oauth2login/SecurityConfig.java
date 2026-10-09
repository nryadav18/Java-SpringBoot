package com.course.oauth2login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*
 * SecurityConfig for an OAuth2 CLIENT application.
 *
 * .oauth2Login() adds two special URLs (filters) to the chain:
 *   /oauth2/authorization/{registrationId}  -> STEP 1: redirect the browser to GitHub/Google/Keycloak
 *   /login/oauth2/code/{registrationId}     -> STEP 4: GitHub sends the browser back here with ?code=...
 *                                              Spring swaps the code for tokens, loads the user,
 *                                              and creates a normal login SESSION (JSESSIONID cookie).
 *
 * This is a browser app with a session cookie -> keep CSRF protection ON (the default).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/error", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
            )
            // Default: if not logged in, show Spring's page listing every configured provider
            // (GitHub, Google ...). With only ONE provider it redirects straight to it.
            .oauth2Login(Customizer.withDefaults())
            // POST /logout ends OUR session (you stay logged in at GitHub - that is normal)
            .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }
}
