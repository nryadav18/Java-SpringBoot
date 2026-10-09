package com.course.k8sdemo.menu;

import com.course.k8sdemo.config.AppProperties;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * MenuController.java
 *
 *   GET /api/menu                -> the menu (business endpoint)
 *   GET /api/info                -> which pod answered + configuration values (secret masked)
 *   GET /api/slow?seconds=5      -> a slow request, to SEE graceful shutdown in action:
 *                                   stop the app while it runs - it still finishes.
 */
@RestController
@RequestMapping("/api")
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    private static final List<MenuItem> MENU = List.of(
            new MenuItem(1, "Paneer Butter Masala", new BigDecimal("9.50"), true),
            new MenuItem(2, "Chicken Biryani", new BigDecimal("11.00"), false),
            new MenuItem(3, "Masala Dosa", new BigDecimal("6.75"), true));

    private final AppProperties props;
    private final Instant startedAt = Instant.now();

    public MenuController(AppProperties props) {
        this.props = props;
    }

    @GetMapping("/menu")
    public Map<String, Object> menu() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("restaurant", props.restaurantName());
        body.put("message", props.welcomeMessage());
        body.put("items", MENU);
        return body;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> body = new LinkedHashMap<>();
        // In Kubernetes the hostname IS the pod name, e.g. menu-service-7c9d8f6b5-x2k4p.
        // Call this several times through the Service and you will see different pods answer.
        body.put("hostname", hostname());
        body.put("startedAt", startedAt.toString());
        body.put("restaurantName", props.restaurantName());
        body.put("welcomeMessage", props.welcomeMessage());
        body.put("paymentApiKey", props.maskedPaymentApiKey()); // NEVER return the real secret
        body.put("javaVersion", System.getProperty("java.version"));
        body.put("maxHeapMb", Runtime.getRuntime().maxMemory() / (1024 * 1024)); // see JVM memory flags
        body.put("cpus", Runtime.getRuntime().availableProcessors());
        return body;
    }

    @GetMapping("/slow")
    public Map<String, Object> slow(@RequestParam(defaultValue = "5") int seconds) throws InterruptedException {
        int safeSeconds = Math.min(Math.max(seconds, 0), 30); // never let a client block a thread forever
        log.info("Slow request started ({} s)", safeSeconds);
        Thread.sleep(safeSeconds * 1000L);
        log.info("Slow request finished");
        return Map.of("sleptSeconds", safeSeconds, "hostname", hostname());
    }

    private static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
