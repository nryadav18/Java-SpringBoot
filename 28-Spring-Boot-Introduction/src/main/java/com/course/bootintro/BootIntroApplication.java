package com.course.bootintro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 28 - Spring Boot Introduction
 * -----------------------------------
 * This is the ENTRY POINT of the whole application - the class with main().
 *
 * @SpringBootApplication is a "3-in-1" annotation:
 *   1. @Configuration            -> this class may define @Bean methods (topics 22/23)
 *   2. @EnableAutoConfiguration  -> "Spring Boot, please look at my classpath and configure things for me"
 *   3. @ComponentScan            -> find @Component/@Service/@RestController classes in THIS package
 *                                   and all its sub-packages (com.course.bootintro.*)
 *
 * How to run:
 *   mvn spring-boot:run                      (from the topic folder)
 *   or click "Run" above main() in VS Code
 * Then open http://localhost:8080/hello in your browser.
 */
@SpringBootApplication
public class BootIntroApplication {

    public static void main(String[] args) {
        // SpringApplication.run(...) does ALL the heavy lifting:
        //   - prints the banner
        //   - reads application.properties
        //   - creates the ApplicationContext (the IoC container from topics 22-27)
        //   - runs auto-configuration (e.g. "Spring MVC is on the classpath -> start Tomcat")
        //   - creates all our beans and starts the embedded web server
        //   - finally calls every CommandLineRunner bean
        SpringApplication.run(BootIntroApplication.class, args);
    }
}
