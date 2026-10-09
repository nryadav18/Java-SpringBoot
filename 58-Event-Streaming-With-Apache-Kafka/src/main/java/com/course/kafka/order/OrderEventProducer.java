package com.course.kafka.order;

/*
 * The PRODUCER. KafkaTemplate.send(topic, key, value) appends a record to a topic.
 *
 * THE KEY MATTERS:
 *   Kafka picks the partition from the key: partition = hash(key) % numberOfPartitions.
 *   Same key -> same partition -> kept IN ORDER.
 *   We use orderId as key, so CREATED -> PAID -> SHIPPED of one order are never mixed up.
 *
 * send() is asynchronous: it returns a CompletableFuture immediately. We attach a
 * callback that logs where the record landed (partition + offset) or the error.
 */
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final String topic;

    public OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate,
                              @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<SendResult<String, OrderEvent>> publish(OrderEvent event) {
        return kafkaTemplate.send(topic, event.orderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        var meta = result.getRecordMetadata();
                        log.info("SENT {} for order {} -> topic={} partition={} offset={}",
                                event.status(), event.orderId(), meta.topic(), meta.partition(), meta.offset());
                    } else {
                        log.error("FAILED to send {} for order {}: {}", event.status(), event.orderId(), ex.getMessage());
                    }
                });
    }
}
