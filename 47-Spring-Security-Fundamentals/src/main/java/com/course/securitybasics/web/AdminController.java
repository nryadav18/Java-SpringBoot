package com.course.securitybasics.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * AdminController - GET /api/admin/** requires hasRole("ADMIN").
 *
 *   curl -u admin:admin123     http://localhost:8080/api/admin/stats -> 200 OK
 *   curl -u student:student123 http://localhost:8080/api/admin/stats -> 403 Forbidden
 *        (we know WHO you are, but you are NOT ALLOWED)
 *   curl                       http://localhost:8080/api/admin/stats -> 401 Unauthorized
 *        (we don't know who you are)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of("students", 1250, "teachers", 84, "courses", 37);
    }
}
