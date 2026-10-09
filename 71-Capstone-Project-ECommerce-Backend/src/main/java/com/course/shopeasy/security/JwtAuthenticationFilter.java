package com.course.shopeasy.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Runs once per request, inside the Spring Security filter chain (topic 49).
 *
 * Steps:
 *   1. Read the header  "Authorization: Bearer eyJhbGciOi..."
 *   2. Ask JwtService whether the token is valid -> get the e-mail.
 *   3. Load the user (still exists? still enabled?).
 *   4. Put an Authentication into the SecurityContext -> the rest of Spring now knows "who" is calling.
 *
 * If there is no token or it is invalid we do NOTHING and just continue. The request is then anonymous,
 * and the authorization rules in SecurityConfig decide: public endpoint -> OK, protected endpoint -> 401.
 *
 * Note: this class is deliberately NOT a @Component. If it were, Spring Boot would ALSO register it as a
 * normal servlet filter, outside the security chain. SecurityConfig creates it with "new" instead.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(BEARER_PREFIX.length()).trim();
            jwtService.extractValidSubject(token).ifPresent(email -> authenticate(email, request));
        }
        chain.doFilter(request, response);
    }

    private void authenticate(String email, HttpServletRequest request) {
        try {
            UserDetails user = userDetailsService.loadUserByUsername(email);
            if (!user.isEnabled()) {
                return;   // disabled account: stay anonymous
            }
            // 3-argument constructor = "authenticated" token. Credentials are null: we never keep passwords around.
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } catch (UsernameNotFoundException ex) {
            // The token is valid but the user was deleted -> treat as anonymous.
        }
    }
}
