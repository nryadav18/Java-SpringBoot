package com.course.kafka.consumer;

/*
 * CONSUMER #2 - a DIFFERENT consumer group: "analytics-service".
 *
 * Because it is a different group, it receives EVERY event too - independently of
 * the notification-service. Each group keeps its own offsets ("bookmarks").
 * (Two consumers in the SAME group would SHARE the partitions instead.)
 *
 * GET /api/analytics shows the counters.
 */
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.kafka.order.OrderEvent;

@RestController
public class AnalyticsConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsConsumer.class);

    private final Map<String, AtomicLong> countsByStatus = new ConcurrentHashMap<>();

    // Another way to read metadata: @Payload for the value, @Header for single headers.
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "analytics-service")
    public void onOrderEvent(@Payload OrderEvent event,
                             @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                             @Header(KafkaHeaders.OFFSET) long offset) {
        long count = countsByStatus.computeIfAbsent(event.status().name(), k -> new AtomicLong()).incrementAndGet();
        log.info("ANALYTICS partition={} offset={} {} events so far: {}", partition, offset, event.status(), count);
    }

    @GetMapping("/api/analytics")
    public Map<String, Long> counts() {
        Map<String, Long> result = new TreeMap<>();
        countsByStatus.forEach((status, counter) -> result.put(status, counter.get()));
        return result;
    }
}
