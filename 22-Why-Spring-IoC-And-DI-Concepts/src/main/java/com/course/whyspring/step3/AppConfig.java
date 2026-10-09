/*
 * STEP 3 - LET SPRING DO IT.
 *
 * This is a Spring "configuration class": a recipe book that tells the
 * Spring container which objects (called BEANS) to create and how to wire them.
 *
 *   @Configuration  -> "Spring, this class contains bean recipes."
 *   @Bean           -> "Spring, call this method ONCE, keep the returned object
 *                       in your container, and give it to anyone who needs it."
 *
 * Compare it with ManualAssembler: same job, but Spring now creates, stores,
 * wires and manages the objects for us.
 */
package com.course.whyspring.step3;

import com.course.whyspring.notification.NotificationService;
import com.course.whyspring.notification.SmsNotificationService;
import com.course.whyspring.order.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // Bean #1 - its name will be the method name: "notificationService".
    // Want email instead? Return new EmailNotificationService() - nothing else changes.
    @Bean
    public NotificationService notificationService() {
        return new SmsNotificationService();
    }

    // Bean #2 - Spring sees that this method NEEDS a NotificationService parameter,
    // finds the matching bean in the container and passes it in. That is DI done by Spring.
    @Bean
    public OrderService orderService(NotificationService notificationService) {
        return new OrderService(notificationService);
    }
}
