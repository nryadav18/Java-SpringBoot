/*
 * Topic 27 - Spring AOP (online shop)
 *
 * Demos (in order):
 *   1. Which beans got proxies? JDK dynamic proxy vs CGLIB proxy
 *   2. A normal call: @Before -> method -> @AfterReturning -> @After
 *   3. A failing call: @Before -> method throws -> @AfterThrowing -> @After
 *   4. Advice that blocks a call (argument binding with args(...))
 *   5. @Around timing with the custom @LogExecutionTime annotation
 *   6. The SELF-INVOCATION gotcha, and the fix
 *
 * How to run (from the topic folder):
 *     mvn -q compile exec:java
 */
package com.course.aop;

import com.course.aop.config.AppConfig;
import com.course.aop.payment.PaymentService;
import com.course.aop.service.BulkOrderService;
import com.course.aop.service.OrderService;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class App {

    public static void main(String[] args) throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            OrderService orderService = context.getBean(OrderService.class);
            PaymentService paymentService = context.getBean(PaymentService.class);

            System.out.println("=== DEMO 1: Proxies ===");
            describeProxy("orderService", orderService);
            describeProxy("paymentService", paymentService);
            System.out.println();

            System.out.println("=== DEMO 2: A normal call ===");
            String orderId = orderService.placeOrder("Keyboard", 2);
            paymentService.pay(orderId, 2998.0);
            System.out.println();

            System.out.println("=== DEMO 3: A call that throws ===");
            try {
                orderService.cancelOrder("ORD-999");
            } catch (IllegalArgumentException e) {
                System.out.println("   main() caught: " + e.getMessage());
            }
            System.out.println();

            System.out.println("=== DEMO 4: Advice that blocks a call ===");
            try {
                orderService.placeOrder("Mouse", 500);
            } catch (IllegalArgumentException e) {
                System.out.println("   main() caught: " + e.getMessage() + "  (placeOrder never ran)");
            }
            System.out.println();

            System.out.println("=== DEMO 5: @Around timing via @LogExecutionTime ===");
            System.out.println("   " + orderService.generateMonthlyReport());
            System.out.println();

            System.out.println("=== DEMO 6a: Self-invocation - inner calls are NOT advised ===");
            orderService.placeBulkOrders(List.of("Cable", "Charger"));
            System.out.println("   ^ only placeBulkOrders was logged; the two inner placeOrder calls were not!");
            System.out.println();

            System.out.println("=== DEMO 6b: The fix - call through another bean ===");
            context.getBean(BulkOrderService.class).placeBulkOrders(List.of("Cable", "Charger"));
            System.out.println("   ^ now every placeOrder call went through the proxy and was logged");
        }
    }

    private static void describeProxy(String name, Object bean) {
        String kind = AopUtils.isJdkDynamicProxy(bean) ? "JDK dynamic proxy"
                : AopUtils.isCglibProxy(bean) ? "CGLIB proxy (subclass)"
                : "not a proxy";
        System.out.println("   " + name + " -> " + bean.getClass().getName() + "  [" + kind + "]");
        System.out.println("      real target class: " + AopUtils.getTargetClass(bean).getSimpleName());
    }
}
