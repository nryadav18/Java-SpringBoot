package com.course.rabbitmq.config;

/*
 * Declares the RabbitMQ "plumbing": exchanges, queues and bindings.
 *
 * Spring Boot's RabbitAdmin finds these @Bean definitions and creates them in
 * RabbitMQ automatically when the app first connects (if they don't exist yet).
 *
 *                       routing key "order.placed"
 *  OrderEventPublisher ------------------------------> [shop.orders.exchange] (TOPIC)
 *                                                         |  binding "order.placed" -> notification queue
 *                                                         |  binding "order.#"      -> analytics queue
 *
 *  notification queue --(rejected after retries)--> [shop.orders.dlx] (DIRECT) --> dead-letter queue
 *
 *  POST /api/announcements --> [shop.announcements.fanout] (FANOUT) --> every bound queue
 */
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitConfig {

    public static final String ORDER_PLACED_KEY = "order.placed";
    public static final String ORDER_CANCELLED_KEY = "order.cancelled";
    public static final String DEAD_LETTER_KEY = "order.notification.dead";

    @Value("${app.rabbit.exchange}")
    private String exchangeName;
    @Value("${app.rabbit.notification-queue}")
    private String notificationQueueName;
    @Value("${app.rabbit.analytics-queue}")
    private String analyticsQueueName;
    @Value("${app.rabbit.dead-letter-exchange}")
    private String deadLetterExchangeName;
    @Value("${app.rabbit.dead-letter-queue}")
    private String deadLetterQueueName;
    @Value("${app.rabbit.broadcast-exchange}")
    private String broadcastExchangeName;
    @Value("${app.rabbit.broadcast-queue}")
    private String broadcastQueueName;

    // ---------------- Main flow: TOPIC exchange ----------------

    @Bean
    public TopicExchange ordersExchange() {
        // durable = survives a RabbitMQ restart; autoDelete=false = keep it even with no queues
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueueName)
                // "If a message here is rejected, send it to this exchange with this key."
                .deadLetterExchange(deadLetterExchangeName)
                .deadLetterRoutingKey(DEAD_LETTER_KEY)
                .build();
    }

    @Bean
    public Queue analyticsQueue() {
        return QueueBuilder.durable(analyticsQueueName).build();
    }

    @Bean
    public Binding notificationBinding() {
        // Only "order.placed" messages go to the notification queue.
        return BindingBuilder.bind(notificationQueue()).to(ordersExchange()).with(ORDER_PLACED_KEY);
    }

    @Bean
    public Binding analyticsBinding() {
        // "#" = zero or more words, so "order.#" matches order.placed, order.cancelled, ...
        // ("*" would match exactly ONE word.)
        return BindingBuilder.bind(analyticsQueue()).to(ordersExchange()).with("order.#");
    }

    // ---------------- Dead letters: DIRECT exchange ----------------

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchangeName, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(deadLetterQueueName).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        // Direct exchange: routing key must match EXACTLY.
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(DEAD_LETTER_KEY);
    }

    // ---------------- Broadcast: FANOUT exchange ----------------

    @Bean
    public FanoutExchange broadcastExchange() {
        return new FanoutExchange(broadcastExchangeName, true, false);
    }

    @Bean
    public Queue broadcastQueue() {
        return QueueBuilder.durable(broadcastQueueName).build();
    }

    @Bean
    public Binding broadcastBinding() {
        // Fanout ignores routing keys: every bound queue gets a copy.
        return BindingBuilder.bind(broadcastQueue()).to(broadcastExchange());
    }

    // ---------------- JSON instead of Java serialization ----------------

    /*
     * By default Spring AMQP would use Java serialization (binary, Java-only, unsafe).
     * With this bean, messages are sent as readable JSON - any language can consume them.
     * Spring Boot automatically plugs this converter into RabbitTemplate AND into the
     * @RabbitListener containers.
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        // The sender adds a "__TypeId__" header with the class name. Only trust our own package
        // when turning JSON back into objects (security: never trust "*" in production).
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.course.rabbitmq.order", "java.lang", "java.util");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
