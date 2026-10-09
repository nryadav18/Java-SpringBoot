package com.course.rabbitmq.notification;

/*
 * Receives broadcast messages from the FANOUT exchange.
 * If you bind more queues to that exchange (for example one per service),
 * EVERY queue gets its own copy of each announcement.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementListener {

    private static final Logger log = LoggerFactory.getLogger(AnnouncementListener.class);

    @RabbitListener(queues = "${app.rabbit.broadcast-queue}")
    public void onAnnouncement(String text) {
        log.info("ANNOUNCEMENT: {}", text);
    }
}
