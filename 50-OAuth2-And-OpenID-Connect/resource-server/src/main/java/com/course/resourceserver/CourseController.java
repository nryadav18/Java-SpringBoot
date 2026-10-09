package com.course.resourceserver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * CourseController - endpoints of the resource server.
 * With a resource server the "principal" is the validated Jwt itself:
 *   @AuthenticationPrincipal Jwt jwt  -> read any claim: jwt.getSubject(), jwt.getClaimAsString("email")
 */
@RestController
@RequestMapping("/api")
public class CourseController {

    @GetMapping("/public/courses")
    public List<String> courses() {
        return List.of("Java Basics", "Spring Boot", "Spring Security");
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt, JwtAuthenticationToken authentication) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", authentication.getName());                       // preferred_username
        result.put("subject", jwt.getSubject());                            // "sub" - stable user id
        result.put("email", jwt.getClaimAsString("email"));
        result.put("issuer", String.valueOf(jwt.getIssuer()));
        result.put("expiresAt", jwt.getExpiresAt());
        result.put("authorities", authentication.getAuthorities().stream().map(Object::toString).sorted().toList());
        return result;
    }

    @GetMapping("/profile")
    public Map<String, Object> profile(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("message", "Your token has the 'profile' scope",
                      "givenName", String.valueOf(jwt.getClaimAsString("given_name")));
    }

    @GetMapping("/admin/report")
    public Map<String, Object> adminReport() {
        return Map.of("enrolledStudents", 4210, "activeCourses", 3);
    }
}
