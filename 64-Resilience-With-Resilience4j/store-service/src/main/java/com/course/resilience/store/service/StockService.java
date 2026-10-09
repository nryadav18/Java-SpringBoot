/*
 * StockService - where the Resilience4j annotations live.
 * ---------------------------------------------------------------
 * Every annotation refers to an INSTANCE NAME ("inventory", "inventorySlow", "deals", "report").
 * The behaviour of each instance (thresholds, wait times, limits) is configured in
 * application.yml under resilience4j.<module>.instances.<name>.
 *
 * How it works: Spring wraps this bean in an AOP proxy (topic 27). Every call from the
 * controller goes through the proxy, which runs the circuit breaker / retry / ... logic
 * around the real method. Consequence: calling an annotated method from INSIDE this class
 * (this.getStock(...)) bypasses the proxy and NOTHING happens (self-invocation gotcha).
 *
 * Default order when several annotations are on one method (outermost first):
 *   Retry ( CircuitBreaker ( RateLimiter ( TimeLimiter ( Bulkhead ( your method ) ) ) ) )
 */
package com.course.resilience.store.service;

import com.course.resilience.store.client.InventoryClient;
import com.course.resilience.store.model.StockInfo;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StockService {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final InventoryClient inventoryClient;

    // A tiny "last known good value" memory used by the fallbacks.
    // Showing slightly old stock is MUCH better than showing an error page.
    private final Map<String, StockInfo> lastKnownStock = new ConcurrentHashMap<>();

    public StockService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    // =====================================================================
    // 1) CIRCUIT BREAKER + RETRY  (instance "inventory")
    // =====================================================================
    // - Retry (outer): a failed call is tried again (3 attempts in total, see YAML).
    // - CircuitBreaker (inner): counts every attempt. When >= 50 % of the recent calls fail,
    //   it OPENS and further calls fail immediately with CallNotPermittedException
    //   (no network call at all) until the wait time is over.
    // - fallbackMethod is on @Retry (the OUTER one). If we put it on @CircuitBreaker, the
    //   inner fallback would "swallow" the error and Retry would never see a failure to retry.
    @Retry(name = "inventory", fallbackMethod = "stockFallback")
    @CircuitBreaker(name = "inventory")
    public StockInfo getStock(String sku) {
        StockInfo fresh = inventoryClient.fetchStock(sku);
        lastKnownStock.put(sku, fresh);       // remember for bad times
        return fresh;
    }

    // ---- Fallbacks: SAME parameters as the original method + ONE exception parameter at the end.
    // ---- Resilience4j picks the fallback whose exception type matches BEST.

    /** Used when the circuit is OPEN - we did not even try the network. */
    private StockInfo stockFallback(String sku, CallNotPermittedException ex) {
        log.warn("Circuit OPEN for inventory -> instant fallback for {}", sku);
        return lastKnownOrUnknown(sku, "Circuit breaker is OPEN - inventory-service is resting");
    }

    /** Used for every other failure (after all retries were used up). */
    private StockInfo stockFallback(String sku, Exception ex) {
        log.warn("inventory call failed after retries ({}: {}) -> fallback for {}",
                ex.getClass().getSimpleName(), ex.getMessage(), sku);
        return lastKnownOrUnknown(sku, "inventory-service failed: " + ex.getClass().getSimpleName());
    }

    // =====================================================================
    // 2) TIME LIMITER  (instance "inventorySlow")
    // =====================================================================
    // TimeLimiter needs an ASYNC return type (CompletableFuture) because it must be able to
    // stop WAITING for a result that runs on another thread. After 2 s (YAML) it gives up,
    // completes with a TimeoutException and the fallback answers instead.
    @TimeLimiter(name = "inventorySlow", fallbackMethod = "stockAsyncFallback")
    public CompletableFuture<StockInfo> getStockWithTimeLimit(String sku) {
        // supplyAsync runs the call on another thread (topic 17). In production pass your own
        // Executor (thread pool) as the second argument instead of the shared common pool.
        return CompletableFuture.supplyAsync(() -> inventoryClient.fetchStock(sku));
    }

    private CompletableFuture<StockInfo> stockAsyncFallback(String sku, Exception ex) {
        log.warn("Time limit hit / failure for {} ({}) -> fallback", sku, ex.getClass().getSimpleName());
        return CompletableFuture.completedFuture(
                lastKnownOrUnknown(sku, "inventory-service too slow (" + ex.getClass().getSimpleName() + ")"));
    }

    // =====================================================================
    // 3) RATE LIMITER  (instance "deals")
    // =====================================================================
    // At most 3 calls per 10 seconds (YAML). Call number 4 is rejected immediately with
    // RequestNotPermitted -> fallback. Protects an expensive operation from being hammered.
    @RateLimiter(name = "deals", fallbackMethod = "dealsFallback")
    public List<String> todaysDeals() {
        return List.of("LAPTOP-1: 10% off", "PHONE-7: free case", "HEADPHONES-3: back soon");
    }

    private List<String> dealsFallback(RequestNotPermitted ex) {
        log.warn("Rate limit reached for deals");
        return List.of("Too many requests - please try again in a few seconds.");
    }

    // =====================================================================
    // 4) BULKHEAD  (instance "report")
    // =====================================================================
    // At most 2 reports may be built AT THE SAME TIME (YAML). A 3rd concurrent caller is
    // rejected immediately (BulkheadFullException) instead of using up all server threads.
    @Bulkhead(name = "report", fallbackMethod = "reportFallback")
    public String buildReport(String sku) throws InterruptedException {
        log.info("Building report for {} ...", sku);
        Thread.sleep(3000);                   // pretend this is heavy work (PDF, big query...)
        return "Stock report for " + sku + " is ready";
    }

    private String reportFallback(String sku, BulkheadFullException ex) {
        log.warn("Bulkhead full - report for {} rejected", sku);
        return "Report service busy (max 2 at a time). Please retry shortly.";
    }

    // ---------------------------------------------------------------------
    private StockInfo lastKnownOrUnknown(String sku, String reason) {
        StockInfo known = lastKnownStock.get(sku);
        if (known != null) {
            return known.withSourceAndNote("fallback-last-known", reason);
        }
        return new StockInfo(sku, -1, "fallback-unknown", reason);
    }
}
