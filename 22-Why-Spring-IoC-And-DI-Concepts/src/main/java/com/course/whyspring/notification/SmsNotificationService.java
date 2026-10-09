/*
 * A second implementation of NotificationService: sends an SMS (simulated).
 * Notice: OrderService never has to change to use this one. That is the whole point.
 */
package com.course.whyspring.notification;

public class SmsNotificationService implements NotificationService {

    @Override
    public void send(String recipient, String message) {
        System.out.println("   [SMS]   To: " + recipient + " | " + message);
    }

    @Override
    public String channel() {
        return "SMS";
    }
}
