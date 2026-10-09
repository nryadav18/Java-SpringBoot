/*
 * Topic 22 - Why Spring? IoC and DI concepts
 *
 * This main class runs the SAME little "place an order and notify the customer"
 * story three times, so you can watch the design improve:
 *
 *   STEP 1 - tight coupling: OrderService does "new EmailSender()" itself
 *   STEP 2 - plain Java DI: interface + constructor injection + manual assembler
 *   STEP 3 - Spring IoC container: @Configuration + @Bean + ApplicationContext
 *
 * How to run (from the topic folder):
 *     mvn -q compile exec:java
 */
package com.course.whyspring;

import com.course.whyspring.notification.NotificationService;
import com.course.whyspring.order.OrderService;
import com.course.whyspring.step1.TightOrderService;
import com.course.whyspring.step2.ManualAssembler;
import com.course.whyspring.step3.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) {

        System.out.println("=== STEP 1: Tight coupling (new inside the class) ===");
        TightOrderService tight = new TightOrderService();
        tight.placeOrder("asha@example.com", "Java Book");
        System.out.println("   Problem: to switch to SMS we must EDIT TightOrderService.");
        System.out.println();

        System.out.println("=== STEP 2: Plain Java DI (interface + constructor + assembler) ===");
        OrderService emailOrders = ManualAssembler.buildOrderService("email");
        emailOrders.placeOrder("ravi@example.com", "Headphones");
        OrderService smsOrders = ManualAssembler.buildOrderService("sms");
        smsOrders.placeOrder("+91-98765-43210", "Coffee Mug");
        System.out.println("   OrderService.java did not change - only the assembler's choice did.");
        System.out.println();

        System.out.println("=== STEP 3: Spring IoC container (@Configuration + @Bean) ===");
        // try-with-resources: the context is closed automatically at the end (Spring cleans up beans).
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            // Ask the container for a ready-made, fully wired bean.
            OrderService orderService = context.getBean(OrderService.class);
            orderService.placeOrder("meera@example.com", "Laptop Stand");

            // Peek inside the container: which beans did Spring create?
            System.out.println("   Beans created from AppConfig:");
            for (String name : context.getBeanDefinitionNames()) {
                if (!name.startsWith("org.springframework")) {   // hide Spring's own internal beans
                    System.out.println("     - " + name);
                }
            }

            // Beans are singletons by default: asking twice returns the SAME object.
            NotificationService first = context.getBean(NotificationService.class);
            NotificationService second = context.getBean(NotificationService.class);
            System.out.println("   Same NotificationService object both times? " + (first == second));
        }
        System.out.println("=== Done ===");
    }
}
