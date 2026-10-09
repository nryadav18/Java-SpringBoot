/*
 * One implementation of NotificationService: sends an email (simulated with println).
 */
package com.course.whyspring.notification;

public class EmailNotificationService implements NotificationService {

    @Override
    public void send(String recipient, String message) {
        System.out.println("   [EMAIL] To: " + recipient + " | " + message);
    }

    @Override
    public String channel() {
        return "EMAIL";
    }
}
