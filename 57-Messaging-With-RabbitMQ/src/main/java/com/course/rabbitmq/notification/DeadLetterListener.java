package com.course.rabbitmq.notification;

/*
 * Listens to the DEAD-LETTER QUEUE (DLQ): messages that failed all retries.
 *
 * We take the raw Message (not a converted object) so we can look at the
 * headers RabbitMQ added, e.g. "x-death" (which queue, why, how many times).
 * In real life: alert someone, store it for manual review, or re-publish later.
 */
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterListener {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterListener.class);

    @RabbitListener(queues = "${app.rabbit.dead-letter-queue}")
    public void onDeadLetter(Message message) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        Object xDeath = message.getMessageProperties().getHeaders().get("x-death");
        log.error("DEAD LETTER received - needs manual attention. body={} x-death={}", body, xDeath);
    }
}
