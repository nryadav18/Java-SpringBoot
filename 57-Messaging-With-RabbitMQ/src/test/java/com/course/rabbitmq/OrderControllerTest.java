package com.course.rabbitmq;

/*
 * These tests run WITHOUT RabbitMQ.
 *
 * 1) @WebMvcTest starts only the web layer. The publisher is replaced by a Mockito mock
 *    (@MockitoBean, Topic 46), so we can check "was an OrderPlacedEvent published?"
 * 2) A plain unit test checks that our JSON MessageConverter turns an event into JSON
 *    and back (the same thing that happens on the wire).
 *
 * Run: mvn test
 */
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.course.rabbitmq.config.RabbitConfig;
import com.course.rabbitmq.order.OrderController;
import com.course.rabbitmq.order.OrderEventPublisher;
import com.course.rabbitmq.order.OrderPlacedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderEventPublisher publisher;

    @Test
    void validOrderIsAcceptedAndPublished() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":"Asha","customerEmail":"asha@example.com",
                                 "product":"Laptop","quantity":2,"unitPrice":100.00}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        ArgumentCaptor<OrderPlacedEvent> captor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(publisher).publishOrderPlaced(captor.capture());
        assertThat(captor.getValue().totalAmount()).isEqualByComparingTo("200.00");
    }

    @Test
    void invalidOrderIsRejectedAndNothingIsPublished() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":"","customerEmail":"nope","product":"Laptop","quantity":0,"unitPrice":1}
                                """))
                .andExpect(status().isBadRequest());

        verify(publisher, never()).publishOrderPlaced(any());
    }

    @Test
    void jsonConverterRoundTrip() {
        ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build(); // supports Instant
        MessageConverter converter = new RabbitConfig().jsonMessageConverter(objectMapper);

        OrderPlacedEvent event = new OrderPlacedEvent("o-1", "Asha", "asha@example.com", "Laptop", 1,
                new BigDecimal("899.99"), Instant.parse("2026-10-09T05:00:00Z"));

        Message message = converter.toMessage(event, new MessageProperties());
        String json = new String(message.getBody(), StandardCharsets.UTF_8);

        assertThat(json).contains("\"customerName\":\"Asha\"");
        assertThat(message.getMessageProperties().getContentType()).isEqualTo("application/json");
        assertThat(message.getMessageProperties().getHeaders()).containsKey("__TypeId__");
        assertThat(converter.fromMessage(message)).isEqualTo(event);
    }
}
