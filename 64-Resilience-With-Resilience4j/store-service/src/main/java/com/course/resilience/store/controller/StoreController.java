/*
 * StoreController - store-service's REST API (each endpoint shows one Resilience4j module).
 *
 *   GET /api/store/stock/{sku}        -> CircuitBreaker + Retry   (instance "inventory")
 *   GET /api/store/stock-fast/{sku}   -> TimeLimiter              (instance "inventorySlow")
 *   GET /api/store/deals              -> RateLimiter              (instance "deals")
 *   GET /api/store/report/{sku}       -> Bulkhead                 (instance "report")
 *
 * Watch the state:  GET /actuator/circuitbreakers , /actuator/circuitbreakerevents , /actuator/health
 */
package com.course.resilience.store.controller;

import com.course.resilience.store.model.StockInfo;
import com.course.resilience.store.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StockService stockService;

    public StoreController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/stock/{sku}")
    public StockInfo stock(@PathVariable String sku) {
        return stockService.getStock(sku);
    }

    // Spring MVC understands CompletableFuture: it frees the request thread and writes the
    // response when the future completes (async request processing).
    @GetMapping("/stock-fast/{sku}")
    public CompletableFuture<StockInfo> stockFast(@PathVariable String sku) {
        return stockService.getStockWithTimeLimit(sku);
    }

    @GetMapping("/deals")
    public List<String> deals() {
        return stockService.todaysDeals();
    }

    @GetMapping("/report/{sku}")
    public String report(@PathVariable String sku) throws InterruptedException {
        return stockService.buildReport(sku);
    }
}
