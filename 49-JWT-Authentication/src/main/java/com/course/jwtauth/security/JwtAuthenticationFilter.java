package com.course.jwtauth.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JwtAuthenticationFilter - our own security filter. Runs ONCE for every request.
 *
 *   Request ──► [ ... ] ──► [ JwtAuthenticationFilter ] ──► [ ... AuthorizationFilter ] ──► Controller
 *
 * Its only job: "If there is a valid Bearer token, tell Spring Security WHO this is."
 *   1. read header  Authorization: Bearer eyJhbGciOi...
 *   2. no header?   -> do nothing, let the request continue as ANONYMOUS
 *                      (AuthorizationFilter later decides: permitAll -> OK, else 401)
 *   3. header?      -> validate token, load user, put an Authentication into the SecurityContext
 *   4. bad token?   -> do nothing (stay anonymous) -> protected URLs answer 401
 *
 * NOTE: this class is deliberately NOT a @Component. If it were, Spring Boot would ALSO
 * register it as a normal servlet filter, and it would run outside the security chain.
 * SecurityConfig creates it with "new" and adds it to the chain exactly once.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Step 2: no token -> nothing to do here
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length());   // cut off "Bearer "

        try {
            String username = jwtService.extractUsername(token);    // throws if fake/expired

            // Only authenticate if nobody did it already for this request
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, user)) {
                    // Step 3: "this request is made by <user> with these roles"
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                }
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            // Step 4: invalid token -> stay anonymous. Never log the token itself!
            log.debug("Rejected JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);   // ALWAYS continue the chain
    }
}
