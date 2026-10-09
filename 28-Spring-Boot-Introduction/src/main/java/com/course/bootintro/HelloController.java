package com.course.bootintro;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Our first REST endpoints.
 *
 * @RestController = "this class answers HTTP requests, and whatever my methods return
 *                    is written straight into the HTTP response body" (as text or JSON).
 * Spring Boot finds this class automatically thanks to @ComponentScan
 * (it lives in the same package as BootIntroApplication).
 *
 * Try it:
 *   http://localhost:8080/hello
 *   http://localhost:8080/hello?name=Asha
 *   http://localhost:8080/info
 * (Topic 31 explains @GetMapping and friends in full detail - here we just say hello.)
 */
@RestController
public class HelloController {

    private final GreetingService greetingService;

    // Constructor injection (topic 24): Spring passes in the GreetingService bean for us.
    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    // GET /hello            -> "Hello, World! Welcome to Spring Boot."
    // GET /hello?name=Asha  -> "Hello, Asha! Welcome to Spring Boot."
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return greetingService.greet(name);
    }

    // Returning a Map (or any object) -> Spring Boot turns it into JSON automatically (Jackson).
    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "app", "boot-intro",
                "framework", "Spring Boot 3.5.6",
                "javaVersion", System.getProperty("java.version"),
                "serverTime", LocalDateTime.now().toString());
    }
}
