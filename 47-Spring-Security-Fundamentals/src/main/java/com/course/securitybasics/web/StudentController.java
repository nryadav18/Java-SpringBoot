package com.course.securitybasics.web;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
 * StudentController - needs a logged-in user (anyRequest().authenticated()).
 *
 * Spring can hand you WHO is calling, right as a method parameter:
 *   - Authentication -> name + authorities (roles) + more
 *   - Principal      -> just the name
 *
 *   curl -u student:student123 http://localhost:8080/api/me
 *   -> {"username":"student","roles":["ROLE_USER"]}
 *
 *   curl -i http://localhost:8080/api/me          (no credentials)
 *   -> HTTP/1.1 401
 */
@RestController
@RequestMapping("/api")
public class StudentController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Map.of("username", authentication.getName(), "roles", roles);
    }

    // A record is a perfect tiny DTO for the JSON body: {"message":"..."}
    public record FeedbackRequest(String message) { }

    /*
     * This POST works with curl ONLY because CSRF is ignored for /api/** in SecurityConfig.
     * Remove that line and the same call returns 403 Forbidden (missing CSRF token).
     */
    @PostMapping("/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> feedback(@RequestBody FeedbackRequest request, Principal principal) {
        return Map.of("from", principal.getName(), "message", request.message(), "status", "received");
    }
}
