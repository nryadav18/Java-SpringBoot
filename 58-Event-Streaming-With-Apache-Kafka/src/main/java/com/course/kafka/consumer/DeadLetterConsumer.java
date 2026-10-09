package com.course.kafka.consumer;

/*
 * Reads the dead-letter topic "orders.DLT".
 *
 * We override the value deserializer for THIS listener only (properties = ...)
 * and read the value as a plain String. That way even a broken, non-JSON record
 * can be displayed. DeadLetterPublishingRecoverer adds headers that explain the failure.
 */
import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);

    @KafkaListener(topics = "${app.kafka.topic}.DLT", groupId = "dlt-inspector",
            properties = "value.deserializer=org.apache.kafka.common.serialization.StringDeserializer")
    public void onDeadLetter(ConsumerRecord<String, String> record) {
        log.error("DLT  key={} value={} reason={} originalOffset={}",
                record.key(), record.value(),
                header(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE),
                header(record, KafkaHeaders.DLT_ORIGINAL_OFFSET));
    }

    private static String header(ConsumerRecord<String, String> record, String name) {
        Header header = record.headers().lastHeader(name);
        if (header == null) {
            return "-";
        }
        // The original offset is stored as an 8-byte number, the message as text.
        if (KafkaHeaders.DLT_ORIGINAL_OFFSET.equals(name) && header.value().length == Long.BYTES) {
            return String.valueOf(java.nio.ByteBuffer.wrap(header.value()).getLong());
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}
