/*
 * STEP 1 - THE PROBLEM (tight coupling)
 *
 * A very ordinary class that "sends" an email (we just print to the console).
 * Nothing is wrong with this class itself - the problem is HOW other classes use it.
 * See TightOrderService.java.
 */
package com.course.whyspring.step1;

public class EmailSender {

    public void sendEmail(String to, String message) {
        System.out.println("   [EMAIL] To: " + to + " | " + message);
    }
}
