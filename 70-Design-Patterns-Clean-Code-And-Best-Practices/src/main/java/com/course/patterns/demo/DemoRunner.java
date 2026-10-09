package com.course.patterns.demo;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/*
 * DemoRunner.java - runs every PatternDemo bean once the application has started.
 *
 * Spring injects ALL PatternDemo beans as a List, sorted by their @Order annotation.
 * (Note: we print with System.out because this is a console DEMO whose output IS the product.
 *  In a real service you would use a logger - topic 30.)
 *
 * @ConditionalOnProperty: the tests set demo.runner.enabled=false so they start quietly.
 */
@Component
@ConditionalOnProperty(name = "demo.runner.enabled", havingValue = "true", matchIfMissing = true)
public class DemoRunner implements CommandLineRunner {

    private final List<PatternDemo> demos;

    public DemoRunner(List<PatternDemo> demos) {
        this.demos = demos;
    }

    @Override
    public void run(String... args) {
        System.out.println("Running " + demos.size() + " demos...");
        int number = 1;
        for (PatternDemo demo : demos) {
            System.out.println();
            System.out.println("=== " + number++ + ". " + demo.title() + " ===");
            demo.run();
        }
        System.out.println();
        System.out.println("All demos finished.");
    }
}
