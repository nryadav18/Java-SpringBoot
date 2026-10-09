/*
 * STEP 2 - THE FIX, PART 1: program to an INTERFACE.
 *
 * An interface is a "contract": it says WHAT can be done (send a notification),
 * not HOW it is done (email? SMS? push?).
 * Classes that need to notify somebody will depend on THIS interface only.
 */
package com.course.whyspring.notification;

public interface NotificationService {

    /** Send a message to a recipient. HOW it is sent is up to the implementation. */
    void send(String recipient, String message);

    /** A short name, handy for printing which implementation is in use. */
    String channel();
}
