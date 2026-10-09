/*
 * InventoryController - stock levels + a chaos switch.
 *
 *   GET  /api/inventory/{sku}                 -> {"sku":"LAPTOP-1","quantity":12,"source":"inventory-service"}
 *   POST /api/inventory/chaos?mode=FAIL       -> every call now answers HTTP 500
 *   POST /api/inventory/chaos?mode=SLOW&delayMs=4000 -> every call now takes 4 seconds
 *   POST /api/inventory/chaos?mode=OK         -> back to normal
 *   GET  /api/inventory/chaos                 -> current mode
 *
 * The mode is kept in an AtomicReference so many request threads can read/write it safely
 * (topic 17 - concurrency).
 */
package com.course.resilience.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    public enum ChaosMode { OK, FAIL, SLOW }

    private final AtomicReference<ChaosMode> mode = new AtomicReference<>(ChaosMode.OK);
    private final AtomicLong slowDelayMs = new AtomicLong(4000);
    private final AtomicInteger callCounter = new AtomicInteger();   // to SEE how many calls really arrive

    private final Map<String, Integer> stock = Map.of(
            "LAPTOP-1", 12,
            "PHONE-7", 40,
            "HEADPHONES-3", 0);

    public record StockResponse(String sku, int quantity, String source) { }

    @GetMapping("/{sku}")
    public StockResponse getStock(@PathVariable String sku) throws InterruptedException {
        int callNo = callCounter.incrementAndGet();
        ChaosMode current = mode.get();
        log.info("Call #{} for {} (chaos mode = {})", callNo, sku, current);

        if (current == ChaosMode.FAIL) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Simulated database crash");
        }
        if (current == ChaosMode.SLOW) {
            Thread.sleep(slowDelayMs.get());
        }
        return new StockResponse(sku, stock.getOrDefault(sku, 0), "inventory-service");
    }

    @PostMapping("/chaos")
    public Map<String, Object> setChaos(@RequestParam ChaosMode mode,
                                        @RequestParam(defaultValue = "4000") long delayMs) {
        this.mode.set(mode);
        this.slowDelayMs.set(delayMs);
        log.warn("Chaos mode switched to {} (delayMs={})", mode, delayMs);
        return Map.of("mode", mode, "delayMs", delayMs);
    }

    @GetMapping("/chaos")
    public Map<String, Object> getChaos() {
        return Map.of("mode", mode.get(), "delayMs", slowDelayMs.get(), "callsReceived", callCounter.get());
    }
}
