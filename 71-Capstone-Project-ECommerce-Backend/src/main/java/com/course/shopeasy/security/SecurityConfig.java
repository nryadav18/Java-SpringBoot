package com.course.shopeasy.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * The security rules of ShopEasy in ONE place (topics 47-49).
 *
 * <pre>
 *  Who                 | What
 *  --------------------+---------------------------------------------------------------
 *  anyone              | register, login, browse products & categories, API docs, health
 *  CUSTOMER            | cart, place / view / cancel own orders
 *  ADMIN               | create / update / delete products & categories, all orders, actuator
 * </pre>
 *
 * The API is STATELESS: no HTTP session, no cookies. Every request proves who it is with a JWT.
 * Because there are no cookies, CSRF protection is not needed and is disabled (topic 47).
 *
 * @EnableMethodSecurity also enables @PreAuthorize on controller methods - we use it as a second
 * "belt and braces" check on admin endpoints (defense in depth).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /** Endpoints that need no token at all. */
    private static final String[] PUBLIC_DOCS_AND_HEALTH = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/actuator/health/**", "/actuator/info",
            "/h2-console/**", "/error"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtService jwtService,
                                            UserDetailsService userDetailsService,
                                            SecurityProblemHandler problemHandler) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())                       // uses the CorsConfigurationSource bean below
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))     // lets the H2 console (an iframe app) work
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(problemHandler)      // 401 as ProblemDetail JSON
                    .accessDeniedHandler(problemHandler))          // 403 as ProblemDetail JSON
            .authorizeHttpRequests(auth -> auth
                    // ---- public ----
                    .requestMatchers(PUBLIC_DOCS_AND_HEALTH).permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/categories/**").permitAll()
                    // ---- admin ----
                    .requestMatchers("/api/v1/admin/**", "/actuator/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/products/**", "/api/v1/categories/**").hasRole("ADMIN") // POST/PUT/DELETE
                    // ---- customer ----
                    .requestMatchers("/api/v1/cart/**", "/api/v1/orders/**").hasRole("CUSTOMER")
                    // ---- everything else: any logged-in user ----
                    .anyRequest().authenticated())
            // Our JWT filter runs before Spring's username/password filter, so the user is known early.
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt: slow on purpose, salted automatically. "Admin@123" becomes something like
     * $2a$10$Qm8f... and a different hash every time it is encoded (topic 47).
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Boot builds an AuthenticationManager from our UserDetailsService + PasswordEncoder beans.
     * We expose it as a bean so AuthService can call authenticate(email, password) on login.
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * CORS: which browser front-ends (other origins) may call this API (topic 32 / 47).
     * Configure with  shopeasy.cors.allowed-origins=https://shop.example.com,...
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${shopeasy.cors.allowed-origins:http://localhost:3000,http://localhost:5173}") List<String> origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Request-Id"));
        config.setExposedHeaders(List.of("Location", "X-Request-Id"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
