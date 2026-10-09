package com.course.scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import com.course.scheduling.report.ReportService;
import com.course.scheduling.user.UserRegisteredEvent;
import com.course.scheduling.user.UserService;

/*
 * Tests for events and @Async.
 * Scheduling is switched OFF (app.scheduling.enabled=false) so no job fires during the test.
 * Run with:  mvn test
 */
@SpringBootTest(properties = "app.scheduling.enabled=false")
@RecordApplicationEvents   // records every event published during each test
class EventsAndAsyncTest {

    @Autowired
    UserService userService;

    @Autowired
    ReportService reportService;

    @Autowired
    ApplicationEvents events;

    @Test
    void registeringAUserPublishesOneUserRegisteredEvent() {
        userService.register("Asha", "asha@example.com");

        assertThat(events.stream(UserRegisteredEvent.class))
                .hasSize(1)
                .first()
                .extracting(UserRegisteredEvent::email)
                .isEqualTo("asha@example.com");
    }

    @Test
    void asyncReportsRunInParallel() {
        long start = System.currentTimeMillis();

        CompletableFuture<Integer> a = reportService.totalSalesToday();
        CompletableFuture<Integer> b = reportService.newCustomersToday();
        CompletableFuture<Integer> c = reportService.openOrders();
        CompletableFuture.allOf(a, b, c).join();

        long took = System.currentTimeMillis() - start;
        // 3 tasks x 1 s each: sequential would be ~3000 ms, parallel is ~1000 ms
        assertThat(took).isLessThan(2500);
        assertThat(a.join()).isEqualTo(15_430);
    }
}
