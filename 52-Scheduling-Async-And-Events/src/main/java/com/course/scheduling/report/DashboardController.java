package com.course.scheduling.report;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * GET  /api/dashboard               -> starts 3 @Async calls in PARALLEL and combines the results (~1 s, not 3 s)
 * POST /api/dashboard/rebuild-index -> starts a fire-and-forget @Async job and returns 202 Accepted at once
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    public record Dashboard(int salesToday, int newCustomers, int openOrders, long timeTakenMs) { }

    private final ReportService reportService;

    public DashboardController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public Dashboard dashboard() {
        long start = System.currentTimeMillis();

        // 1) START all three tasks - each call returns immediately with a "promise"
        CompletableFuture<Integer> sales = reportService.totalSalesToday();
        CompletableFuture<Integer> customers = reportService.newCustomersToday();
        CompletableFuture<Integer> orders = reportService.openOrders();

        // 2) WAIT until ALL of them are done
        CompletableFuture.allOf(sales, customers, orders).join();

        // 3) Read the results (join() does not block any more - they are finished)
        return new Dashboard(sales.join(), customers.join(), orders.join(),
                System.currentTimeMillis() - start);
    }

    @PostMapping("/rebuild-index")
    public ResponseEntity<Map<String, String>> rebuildIndex() {
        reportService.rebuildSearchIndex();   // returns immediately
        return ResponseEntity.accepted().body(Map.of("status", "Rebuild started in the background - watch the log"));
    }
}
