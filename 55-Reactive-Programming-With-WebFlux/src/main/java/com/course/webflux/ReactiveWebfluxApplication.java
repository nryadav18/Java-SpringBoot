package com.course.webflux;

/*
 * Topic 55 - Reactive Programming with WebFlux
 * ---------------------------------------------
 * Entry point of the app. Because we added spring-boot-starter-webflux
 * (and NOT spring-boot-starter-web), Spring Boot starts a NETTY server
 * instead of Tomcat. Netty uses a few "event loop" threads that never wait.
 *
 * Run:  mvn spring-boot:run   then open http://localhost:8080/api/products
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactiveWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveWebfluxApplication.class, args);
    }
}
