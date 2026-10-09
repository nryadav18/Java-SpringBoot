package com.course.bootintro;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * The test that start.spring.io generates for every new project (plus one small assertion).
 * @SpringBootTest starts the WHOLE application context. If any bean fails to start,
 * this test fails - a cheap "smoke test". Run with: mvn test
 * (Topic 46 covers Spring Boot testing in depth.)
 */
@SpringBootTest
class BootIntroApplicationTests {

    @Autowired
    private GreetingService greetingService;

    @Test
    void contextLoads() {
        // If we reach this line, the application started successfully.
    }

    @Test
    void greetingServiceSaysHello() {
        assertThat(greetingService.greet("Ravi")).isEqualTo("Hello, Ravi! Welcome to Spring Boot.");
    }
}
