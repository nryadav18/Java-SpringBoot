package com.course.scheduling.report;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 * @Async methods that RETURN a result.
 *
 * Each method pretends to run a slow query (1 second). Called one after the other they would take
 * 3 seconds. Because they are @Async, all three run AT THE SAME TIME on the "async-" pool,
 * so the dashboard is ready in about 1 second.
 *
 * Return type rules for @Async:
 *   void                  -> fire-and-forget (exceptions go to the AsyncUncaughtExceptionHandler)
 *   CompletableFuture<T>  -> the caller gets a "promise" and can wait / combine results
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    @Async
    public CompletableFuture<Integer> totalSalesToday() {
        log.info("Calculating total sales ...");
        sleep(1000);
        return CompletableFuture.completedFuture(15_430);
    }

    @Async
    public CompletableFuture<Integer> newCustomersToday() {
        log.info("Counting new customers ...");
        sleep(1000);
        return CompletableFuture.completedFuture(42);
    }

    @Async
    public CompletableFuture<Integer> openOrders() {
        log.info("Counting open orders ...");
        sleep(1000);
        return CompletableFuture.completedFuture(7);
    }

    /**
     * Fire-and-forget method that FAILS. Nobody is waiting for it, so the exception is
     * handed to AsyncConfig.getAsyncUncaughtExceptionHandler() which logs it.
     */
    @Async
    public void rebuildSearchIndex() {
        log.info("Rebuilding the search index ...");
        sleep(500);
        throw new IllegalStateException("Search engine is not reachable");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
