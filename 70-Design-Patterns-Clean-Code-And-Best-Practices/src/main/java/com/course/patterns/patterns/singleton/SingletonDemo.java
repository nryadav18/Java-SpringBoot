package com.course.patterns.patterns.singleton;

import com.course.patterns.demo.PatternDemo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * SINGLETON (creational pattern)
 * "Exactly ONE instance of a class, with a global access point."
 *
 * 1) Plain Java: the ENUM singleton - the simplest thread-safe way (Effective Java, item 3).
 * 2) Spring    : every @Component / @Bean is a singleton BY DEFAULT (scope "singleton").
 *                But it is "one per ApplicationContext", created and managed by Spring, and
 *                still INJECTED (testable) - no static getInstance() calls in your code.
 *                That is why you almost never write the classic Singleton by hand in Spring apps.
 *
 * Expected output:
 *   [java  ] same enum instance? true  - currency = INR
 *   [spring] getBean twice -> same object? true
 *   [spring] the bean Spring injected into DemoRunner is this very object? true
 */
@Component
@Order(10)
public class SingletonDemo implements PatternDemo {

    /** Classic Java singleton using an enum. The JVM guarantees one instance, even with threads. */
    enum AppSettings {
        INSTANCE;

        private final Map<String, String> values = new ConcurrentHashMap<>(Map.of("currency", "INR"));

        String get(String key) {
            return values.get(key);
        }
    }

    private final ApplicationContext context;

    public SingletonDemo(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public String title() {
        return "Singleton - one instance (enum + Spring singleton scope)";
    }

    @Override
    public void run() {
        AppSettings a = AppSettings.INSTANCE;
        AppSettings b = AppSettings.INSTANCE;
        System.out.println("[java  ] same enum instance? " + (a == b) + "  - currency = " + a.get("currency"));

        Object first = context.getBean(SingletonDemo.class);
        Object second = context.getBean(SingletonDemo.class);
        System.out.println("[spring] getBean twice -> same object? " + (first == second));
        System.out.println("[spring] the bean Spring injected into DemoRunner is this very object? " + (first == this));
    }
}
