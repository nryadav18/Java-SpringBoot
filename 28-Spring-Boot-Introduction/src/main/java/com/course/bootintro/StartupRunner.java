package com.course.bootintro;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/*
 * CommandLineRunner = "run this code ONCE, right after the application has fully started".
 *
 * Real-world uses: load sample data, print useful startup info, warm up a cache,
 * check that an external system is reachable.
 *
 * Spring Boot calls run(...) automatically for every bean that implements CommandLineRunner.
 */
@Component
public class StartupRunner implements CommandLineRunner {

    private final ApplicationContext context;
    private final GreetingService greetingService;

    public StartupRunner(ApplicationContext context, GreetingService greetingService) {
        this.context = context;
        this.greetingService = greetingService;
    }

    @Override
    public void run(String... args) {
        System.out.println("==================================================");
        System.out.println(" StartupRunner: the application is up and running!");
        System.out.println(" Command-line args : " + Arrays.toString(args));
        System.out.println(" Beans in container: " + context.getBeanDefinitionCount());
        System.out.println(" Quick self-test   : " + greetingService.greet("Startup"));
        System.out.println(" Try it            : http://localhost:8080/hello?name=YourName");
        System.out.println("==================================================");
        // We use System.out here only to keep the first lesson simple.
        // From topic 30 onwards we use a proper logger instead.
    }
}
