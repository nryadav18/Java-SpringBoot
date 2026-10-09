package com.course.resourceserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/*
 * SecurityConfig for a RESOURCE SERVER.
 *
 * .oauth2ResourceServer(o -> o.jwt(...)) adds the BearerTokenAuthenticationFilter.
 * For every request with "Authorization: Bearer <jwt>" it:
 *   1. downloads (once, then caches) the issuer's PUBLIC keys from its JWKS endpoint
 *   2. checks the signature, "exp" (not expired), "iss" (the issuer we trust)
 *   3. converts the claims into authorities (our KeycloakRealmRoleConverter)
 *   4. stores a JwtAuthenticationToken in the SecurityContext
 *
 * Compare with topic 49: there we wrote JwtService + JwtAuthenticationFilter ourselves.
 * Here Spring does all of it - we only configure WHICH issuer to trust (application.yml).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // stateless token API, no cookies
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/error").permitAll()
                .requestMatchers("/api/profile").hasAuthority("SCOPE_profile") // a SCOPE from the token
                .requestMatchers("/api/admin/**").hasRole("ADMIN")              // a ROLE from realm_access
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    /** Plugs our Keycloak role converter into Spring's JWT -> Authentication conversion. */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        // Use the human-friendly "preferred_username" as Authentication.getName() instead of "sub" (a UUID)
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }
}
