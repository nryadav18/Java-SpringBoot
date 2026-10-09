package com.course.securitybasics.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * PublicController - URLs under /api/public/** are permitAll() in SecurityConfig.
 * Anyone can call them without logging in.
 *
 *   curl http://localhost:8080/api/public/notice
 *   -> {"notice":"College is closed on Friday for the festival."}
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/notice")
    public Map<String, String> notice() {
        return Map.of("notice", "College is closed on Friday for the festival.");
    }
}
