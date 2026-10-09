package com.course.dbsecurity.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
 * SecurityConfig - URL rules + method security switch.
 *
 * Two layers of protection in this project:
 *   1. URL rules (here, in the filter chain)     -> "who may enter this door?"
 *   2. Method rules (@PreAuthorize on services)  -> "who may do this action, with THIS data?"
 *
 * @EnableMethodSecurity switches ON @PreAuthorize / @PostAuthorize annotations.
 * Without it those annotations are silently IGNORED (a classic mistake!).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Anyone may register (otherwise nobody could ever create an account!)
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                // H2 web console - for learning only, NEVER expose it in production
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/error").permitAll()
                // Role-based URL rules. hasRole("ADMIN") == hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Permission-based rule: exact authority string, no ROLE_ prefix
                .requestMatchers("/api/reports/**").hasAuthority("REPORT_READ")
                // Everything else: any logged-in user
                .anyRequest().authenticated()
            )
            // REST API: credentials on every request via HTTP Basic, no server session needed
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Stateless API with no cookies -> CSRF protection is not needed
            .csrf(csrf -> csrf.disable())
            // The H2 console uses HTML frames; allow frames from the same origin only
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Notice: there is NO UserDetailsService bean here. JpaUserDetailsService is a @Service,
     * so Spring Boot finds it and wires it into the login process automatically, together
     * with the PasswordEncoder above.
     */
}
