package com.course.scheduling.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/*
 * Spring itself publishes events too! ApplicationReadyEvent is published when the
 * application has fully started and is ready to serve requests.
 * Other built-in ones: ContextRefreshedEvent, ContextClosedEvent, ApplicationStartedEvent ...
 */
@Component
public class StartupListener {

    private static final Logger log = LoggerFactory.getLogger(StartupListener.class);

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("=== Application is READY. Try: POST /api/users and GET /api/dashboard (see requests.http) ===");
    }
}
