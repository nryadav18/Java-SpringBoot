package com.course.bootintro;

import org.springframework.stereotype.Service;

/*
 * A tiny service bean. Nothing Spring-Boot-specific here: it is the same @Service
 * you met in topic 23. Spring Boot still uses the normal Spring container underneath.
 */
@Service
public class GreetingService {

    public String greet(String name) {
        return "Hello, " + name + "! Welcome to Spring Boot.";
    }
}
