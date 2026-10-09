package com.course.patterns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 70 - Design Patterns, Clean Code and Best Practices.
 *
 * Package map (one package per idea, so you can study them one at a time):
 *   solid/srp, solid/ocp, solid/lsp, solid/isp, solid/dip   -> the 5 SOLID principles (bad vs good)
 *   patterns/singleton, factory, builder, adapter            -> creational / structural patterns
 *   patterns/strategy, observer, templatemethod, proxy       -> behavioural patterns + how SPRING uses them
 *   cleancode                                                -> before/after refactoring
 *   demo                                                     -> PatternDemo interface + DemoRunner
 *
 * Run:  mvn spring-boot:run      (prints every demo in order, then exits)
 */
@SpringBootApplication
public class PatternsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatternsApplication.class, args);
    }
}
