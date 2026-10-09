/*
 * Why DI makes testing easy.
 *
 * Because OrderService receives its NotificationService through the constructor,
 * a test can hand it a FAKE implementation that just records what was sent.
 * No real email, no SMS gateway, no Spring container needed.
 *
 * Run with:  mvn -q test
 */
package com.course.whyspring.order;

import com.course.whyspring.notification.NotificationService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderServiceTest {

    /** A hand-written fake: remembers messages instead of sending them. */
    static class FakeNotificationService implements NotificationService {
        final List<String> sent = new ArrayList<>();

        @Override
        public void send(String recipient, String message) {
            sent.add(recipient + " -> " + message);
        }

        @Override
        public String channel() {
            return "FAKE";
        }
    }

    @Test
    void placeOrderSendsExactlyOneConfirmation() {
        // Arrange: inject the fake by hand
        FakeNotificationService fake = new FakeNotificationService();
        OrderService service = new OrderService(fake);

        // Act
        service.placeOrder("test@example.com", "Pen");

        // Assert
        assertEquals(1, fake.sent.size());
        assertEquals("test@example.com -> Your order for Pen is confirmed!", fake.sent.get(0));
    }
}
