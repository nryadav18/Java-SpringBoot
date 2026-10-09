/*
 * STEP 2 - THE FIX, PART 3: a hand-written "assembler" (a tiny home-made container).
 *
 * Somebody must still call "new" - but now it happens in ONE place only.
 * This class:
 *   1. decides WHICH implementation to use   (configuration)
 *   2. CREATES the objects                     (object creation)
 *   3. WIRES them together                     (dependency injection)
 *
 * Control over object creation has moved OUT of OrderService and INTO this assembler.
 * That move is called INVERSION OF CONTROL (IoC).
 *
 * Spring's ApplicationContext is exactly this idea - just automatic, powerful and battle-tested.
 */
package com.course.whyspring.step2;

import com.course.whyspring.notification.EmailNotificationService;
import com.course.whyspring.notification.NotificationService;
import com.course.whyspring.notification.SmsNotificationService;
import com.course.whyspring.order.OrderService;

public class ManualAssembler {

    /** Builds a fully wired OrderService. Pass "sms" or "email" to switch the channel. */
    public static OrderService buildOrderService(String channel) {
        NotificationService notifier;
        if ("sms".equalsIgnoreCase(channel)) {
            notifier = new SmsNotificationService();
        } else {
            notifier = new EmailNotificationService();
        }
        return new OrderService(notifier);   // inject the dependency through the constructor
    }
}
