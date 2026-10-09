/*
 * MessageController.java  (Topic 62 - product-service)
 *
 * WHAT: Shows the SECOND way to get a refreshable value: @Value + @RefreshScope.
 *
 * WHY @RefreshScope?
 *       @Value is injected ONCE, when the bean is created. Normally a singleton bean is
 *       created only once, so the value would never change.
 *       @RefreshScope makes Spring give us a smart proxy. On POST /actuator/refresh the
 *       real bean behind the proxy is thrown away; the next request creates a NEW bean,
 *       and the new bean gets the NEW @Value. (Think: "re-print the menu card".)
 *
 * TRY:  GET http://localhost:8081/api/message
 */
package com.course.configdemo.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RefreshScope
public class MessageController {

    private final String welcomeMessage;
    private final String companyName;
    private final Environment environment;
    // When was THIS bean object created? After a refresh you will see a new time.
    private final LocalTime beanCreatedAt = LocalTime.now().withNano(0);

    public MessageController(@Value("${shop.welcome-message}") String welcomeMessage,
                             @Value("${shop.company-name}") String companyName,
                             Environment environment) {
        this.welcomeMessage = welcomeMessage;
        this.companyName = companyName;
        this.environment = environment;
    }

    @GetMapping("/api/message")
    public Map<String, Object> message() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("welcomeMessage", welcomeMessage);
        result.put("companyName", companyName);
        result.put("activeProfiles", Arrays.toString(environment.getActiveProfiles()));
        result.put("beanCreatedAt", beanCreatedAt.toString());
        return result;
    }
}
