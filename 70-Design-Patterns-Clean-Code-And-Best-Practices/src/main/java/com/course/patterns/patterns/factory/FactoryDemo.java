package com.course.patterns.patterns.factory;

import com.course.patterns.demo.PatternDemo;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * FACTORY (creational pattern)
 * "Put the 'which class do I create?' decision in ONE place."
 * Callers ask the factory for a Notification and never write "new EmailNotification()".
 *
 * In Spring:
 *   - The whole container is a giant factory: BeanFactory / ApplicationContext.getBean(...)
 *   - Every @Bean method in a @Configuration class is a FACTORY METHOD.
 *   - FactoryBean<T> lets you plug custom creation logic into the container.
 *
 * Expected output:
 *   EMAIL to asha@example.com: Your order has shipped
 *   SMS to +91-98000-00000: Your order has shipped
 *   PUSH to device-42: Your order has shipped
 *   Unknown channel 'pigeon' -> Unknown notification channel: pigeon
 */
@Component
@Order(11)
public class FactoryDemo implements PatternDemo {

    /** What every product of the factory can do. */
    interface Notification {
        String send(String to, String message);
    }

    static class EmailNotification implements Notification {
        public String send(String to, String message) { return "EMAIL to " + to + ": " + message; }
    }

    static class SmsNotification implements Notification {
        public String send(String to, String message) { return "SMS to " + to + ": " + message; }
    }

    static class PushNotification implements Notification {
        public String send(String to, String message) { return "PUSH to " + to + ": " + message; }
    }

    /** The factory: the ONLY place that knows the concrete classes. */
    static final class NotificationFactory {
        private NotificationFactory() {
        }

        static Notification create(String channel) {
            return switch (channel.toLowerCase()) {          // Java 17 switch expression
                case "email" -> new EmailNotification();
                case "sms" -> new SmsNotification();
                case "push" -> new PushNotification();
                default -> throw new IllegalArgumentException("Unknown notification channel: " + channel);
            };
        }
    }

    @Override
    public String title() {
        return "Factory - one place decides which object to create";
    }

    @Override
    public void run() {
        String message = "Your order has shipped";
        System.out.println(NotificationFactory.create("email").send("asha@example.com", message));
        System.out.println(NotificationFactory.create("SMS").send("+91-98000-00000", message));
        System.out.println(NotificationFactory.create("push").send("device-42", message));
        try {
            NotificationFactory.create("pigeon");
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown channel 'pigeon' -> " + e.getMessage());
        }
    }
}
