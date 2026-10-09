package com.course.kafka.config;

/*
 * What happens when a @KafkaListener throws an exception?
 *
 * Kafka has no "reject this message" like RabbitMQ. The consumer simply decides
 * what to do and then moves its offset forward. Spring's DefaultErrorHandler does:
 *
 *   1. retry the same record a few times (here: 2 retries, 1 second apart)
 *   2. then call the "recoverer": DeadLetterPublishingRecoverer writes the record
 *      to "<topic>.DLT" (with extra headers: exception message, original offset ...)
 *   3. commit the offset, so the consumer continues with the NEXT record.
 *
 * Spring Boot automatically plugs a single CommonErrorHandler bean into the
 * @KafkaListener container factory - so declaring this bean is enough.
 */
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaErrorHandlingConfig.class);

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> jsonTemplate,
                                                 ProducerFactory<String, Object> producerFactory) {

        // Records that could not even be deserialized (not valid JSON - a "poison pill")
        // only exist as raw bytes, so they need a template that writes raw bytes.
        KafkaTemplate<String, byte[]> bytesTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                producerFactory.getConfigurationProperties(), new StringSerializer(), new ByteArraySerializer()));

        Map<Class<?>, KafkaOperations<?, ?>> templates = new LinkedHashMap<>();
        templates.put(byte[].class, bytesTemplate);   // broken records -> raw bytes
        templates.put(Object.class, jsonTemplate);    // normal records -> JSON

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(templates);

        // FixedBackOff(interval ms, max retries): 1 first try + 2 retries = 3 attempts in total.
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));

        // Retrying can never fix invalid data, so send these straight to the DLT.
        handler.addNotRetryableExceptions(IllegalArgumentException.class);

        handler.setRetryListeners((record, ex, attempt) ->
                log.warn("Attempt {} failed for key={} offset={}: {}",
                        attempt, record.key(), record.offset(), ex.getMessage()));
        return handler;
    }
}
