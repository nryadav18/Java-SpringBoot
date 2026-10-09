package com.course.kafka.config;

/*
 * Topics as Spring beans.
 *
 * Spring Boot's KafkaAdmin finds every NewTopic bean at startup and creates the
 * topic in Kafka if it does not exist yet (it never deletes or shrinks topics).
 */
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ordersTopic(@Value("${app.kafka.topic}") String topic,
                                @Value("${app.kafka.partitions}") int partitions) {
        return TopicBuilder.name(topic)
                .partitions(partitions)   // 3 partitions = up to 3 consumers of ONE group can work in parallel
                .replicas(1)              // copies on different brokers; we only have 1 broker locally
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(7L * 24 * 60 * 60 * 1000)) // keep 7 days
                .build();
    }

    /*
     * Dead-letter topic. By convention Spring's DeadLetterPublishingRecoverer sends a failed
     * record from topic "orders" to "orders.DLT", into the SAME partition number,
     * so the DLT needs at least as many partitions as the original topic.
     */
    @Bean
    public NewTopic ordersDeadLetterTopic(@Value("${app.kafka.topic}") String topic,
                                          @Value("${app.kafka.partitions}") int partitions) {
        return TopicBuilder.name(topic + ".DLT")
                .partitions(partitions)
                .replicas(1)
                .build();
    }
}
