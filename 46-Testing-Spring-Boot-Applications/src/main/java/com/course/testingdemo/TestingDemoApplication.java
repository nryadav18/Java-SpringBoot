package com.course.testingdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 46 - Testing Spring Boot Applications
 *
 * The app itself is a small Library "Book API" (you know this style from topics 33-38).
 * The STAR of this topic is the src/test/java folder:
 *
 *   BookServiceTest          unit test        (Mockito, no Spring)        - milliseconds
 *   BookRepositoryTest       @DataJpaTest     (JPA slice + H2)            - ~seconds
 *   BookControllerTest       @WebMvcTest      (web slice + MockMvc)       - ~seconds
 *   BookApiIntegrationTest   @SpringBootTest  (whole app, real HTTP)      - slowest
 *
 * Run the app : mvn spring-boot:run
 * Run tests   : mvn test
 */
@SpringBootApplication
public class TestingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestingDemoApplication.class, args);
    }
}
