package com.course.securitybasics.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
 * SecurityConfig - the ONE place where we describe our security rules.
 *
 * As soon as we declare our own SecurityFilterChain bean, Spring Boot's default
 * ("lock everything + generated password") backs off and uses OUR rules instead.
 *
 * Beans in this class:
 *   1. SecurityFilterChain       -> WHICH urls need WHAT (authorization) + HOW users log in
 *   2. UserDetailsService        -> WHERE users come from (here: memory; topic 48: database)
 *   3. PasswordEncoder           -> HOW passwords are hashed and compared (BCrypt)
 *   4. CorsConfigurationSource   -> which OTHER websites (origins) may call us from a browser
 */
@Configuration
@EnableWebSecurity // optional in Spring Boot (auto-config adds it), but it documents intent
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ---- 1. Authorization rules: checked TOP to BOTTOM, the FIRST match wins ----
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/error").permitAll()          // open to everyone
                .requestMatchers(HttpMethod.GET, "/api/admin/**").hasRole("ADMIN") // ADMIN only
                .anyRequest().authenticated()                                     // everything else: just log in
            )
            // ---- 2. HOW can a user prove who they are? (both are allowed at the same time) ----
            .httpBasic(Customizer.withDefaults())  // header "Authorization: Basic base64(user:pass)" - curl/Postman
            .formLogin(Customizer.withDefaults())  // HTML login page at /login - for humans in a browser
            .logout(Customizer.withDefaults())     // POST /logout ends the session

            // ---- 3. CSRF: keep it ON for the browser pages (form login uses a session cookie),
            //         ignore it only for our REST API that curl/Postman call with Basic auth.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

            // ---- 4. CORS: answer the browser's preflight using the bean below ----
            .cors(Customizer.withDefaults());

        return http.build();
    }

    /*
     * Users kept in memory - perfect for learning and demos, NOT for production
     * (users vanish on restart and nobody can register). Topic 48 uses a database.
     *
     * Notice: we NEVER store "student123" itself. We store its BCrypt hash.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails student = User.withUsername("student")
                .password(encoder.encode("student123"))
                .roles("USER")                 // stored as authority "ROLE_USER"
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("USER", "ADMIN")        // "ROLE_USER", "ROLE_ADMIN"
                .build();

        return new InMemoryUserDetailsManager(student, admin);
    }

    /*
     * BCrypt = slow, salted, one-way hash. At login Spring calls
     * encoder.matches(typedPassword, storedHash) - it never "decrypts" anything.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // default strength 10 (2^10 = 1024 rounds)
    }

    /*
     * CORS is a BROWSER rule. Without this, JavaScript running on http://localhost:3000
     * may not read responses from http://localhost:8080.
     * With Spring Security on, configure CORS through security (.cors(...)) so the
     * browser's "preflight" OPTIONS request is answered BEFORE any login check.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
