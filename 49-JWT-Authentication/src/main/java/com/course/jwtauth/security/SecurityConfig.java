package com.course.jwtauth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * SecurityConfig - a STATELESS filter chain for a token-based API.
 *
 * Compared with topic 47/48:
 *   - no session  (SessionCreationPolicy.STATELESS): the server remembers NOTHING between requests
 *   - no CSRF     : we use no cookies, so a malicious site cannot "ride" on them
 *   - no Basic, no form login: the ONLY way in is a Bearer token, checked by our filter
 *   - custom 401 JSON response for "who are you?" failures
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserDetailsService userDetailsService) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/error").permitAll()   // login & register are open
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // No Basic/form login configured -> we must say what a 401 looks like ourselves.
            // (Without this, Spring Security would answer 403 for anonymous users.)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jsonUnauthorizedEntryPoint()))
            // Put our JWT filter BEFORE the standard username/password filter
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                             UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * AuthenticationManager = the "check username + password" service. AuthController uses it.
     * Spring Boot already builds one from our UserDetailsService + PasswordEncoder;
     * this bean simply makes it injectable.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 401 + small JSON body + the standard "WWW-Authenticate: Bearer" header. */
    private AuthenticationEntryPoint jsonUnauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("WWW-Authenticate", "Bearer");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Missing, invalid or expired token\"}");
        };
    }
}
