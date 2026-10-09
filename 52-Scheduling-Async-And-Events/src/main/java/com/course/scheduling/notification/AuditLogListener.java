package com.course.scheduling.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.course.scheduling.user.UserRegisteredEvent;

/*
 * LISTENER #2 - write an audit log line.
 *
 * A plain @EventListener is SYNCHRONOUS:
 *   - it runs immediately when publishEvent() is called,
 *   - on the SAME thread as the publisher (look for [nio-8080-exec-N] in the log),
 *   - inside the SAME transaction.
 * If it throws an exception, the exception travels back to publishEvent() and the
 * registration transaction is rolled back. Keep synchronous listeners small and safe.
 *
 * @Order decides the order when several synchronous listeners handle the same event (lower = first).
 */
@Component
public class AuditLogListener {

    private static final Logger log = LoggerFactory.getLogger(AuditLogListener.class);

    @EventListener
    @Order(1)
    public void audit(UserRegisteredEvent event) {
        log.info("AUDIT: user {} ({}) registered at {}", event.userId(), event.email(), event.registeredAt());
    }

    /**
     * condition = SpEL filter: this listener only reacts to company email addresses.
     */
    @EventListener(condition = "#event.email().endsWith('@course.com')")
    @Order(2)
    public void flagStaffAccount(UserRegisteredEvent event) {
        log.info("AUDIT: {} is a STAFF account - flagging for review", event.email());
    }
}
