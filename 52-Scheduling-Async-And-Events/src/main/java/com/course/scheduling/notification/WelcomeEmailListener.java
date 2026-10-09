package com.course.scheduling.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.course.scheduling.user.UserRegisteredEvent;

/*
 * LISTENER #1 - send the welcome email.
 *
 * @TransactionalEventListener(phase = AFTER_COMMIT)
 *     -> runs only AFTER the registration transaction has been COMMITTED.
 *        If the transaction rolls back, the user does not exist, so NO email is sent. Exactly what we want!
 *
 * @Async
 *     -> runs on a thread from "appTaskExecutor" (thread names "async-1", "async-2" ...),
 *        so the HTTP request does not wait 3 seconds for the email.
 *
 * In this course the email is only LOGGED. Topic 54 shows how to really send emails.
 */
@Component
public class WelcomeEmailListener {

    private static final Logger log = LoggerFactory.getLogger(WelcomeEmailListener.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendWelcomeEmail(UserRegisteredEvent event) throws InterruptedException {
        log.info("Sending welcome email to {} ...", event.email());
        Thread.sleep(3000);   // pretend the mail server is slow
        log.info("Welcome email SENT to {} <{}>", event.name(), event.email());
    }
}
