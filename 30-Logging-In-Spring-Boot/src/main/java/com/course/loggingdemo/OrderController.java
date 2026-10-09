package com.course.loggingdemo;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * REST endpoints that trigger different kinds of log messages.
 *
 *   POST /orders?customer=Asha&item=Pizza&quantity=2   -> INFO (and DEBUG) logs
 *   POST /orders?customer=Asha&item=Pizza&quantity=99  -> WARN log, 400 response
 *   GET  /orders/1001                                  -> found / WARN if missing
 *   POST /orders/1001/notify                           -> ERROR log with stack trace
 *   GET  /orders/levels-demo                           -> one message at EVERY level
 *
 * (Request handling details are covered in topics 31-32; focus on the logging here.)
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestParam String customer,
                                        @RequestParam String item,
                                        @RequestParam int quantity,
                                        @RequestParam(defaultValue = "4111111111111111") String card) {
        log.debug("HTTP request to place an order received");
        try {
            return ResponseEntity.ok(orderService.placeOrder(customer, item, quantity, card));
        } catch (IllegalArgumentException ex) {
            // Expected problem caused by the client -> WARN without stack trace is enough.
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable long id) {
        return orderService.findOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/notify")
    public Map<String, String> notifyRestaurant(@PathVariable long id) {
        orderService.notifyRestaurant(id);
        return Map.of("result", "Notification attempted - check the ERROR log with stack trace");
    }

    @GetMapping("/levels-demo")
    public Map<String, String> levelsDemo() {
        // Which of these lines you SEE depends on the level configured for this package.
        log.trace("TRACE - the tiniest details (hidden unless level is TRACE)");
        log.debug("DEBUG - developer details");
        log.info("INFO  - normal business event");
        log.warn("WARN  - something odd, but we coped");
        log.error("ERROR - something failed");
        // isDebugEnabled() guards EXPENSIVE work that is only needed for the log message.
        if (log.isDebugEnabled()) {
            log.debug("Expensive debug info: {}", buildExpensiveReport());
        }
        return Map.of("result", "Look at the console / logs/food-delivery.log");
    }

    private String buildExpensiveReport() {
        return "report built at " + System.currentTimeMillis();
    }
}
