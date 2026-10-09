package com.course.kafka;

/*
 * Tests that run WITHOUT a Kafka broker.
 *
 * 1) @WebMvcTest starts only the web layer; the producer is a Mockito mock.
 * 2) A plain unit test sends an OrderEvent through the same JsonSerializer /
 *    JsonDeserializer pair that the app uses on the wire.
 *
 * (For real end-to-end tests teams use Testcontainers or spring-kafka-test's @EmbeddedKafka.)
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
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.course.kafka.order.OrderController;
import com.course.kafka.order.OrderEvent;
import com.course.kafka.order.OrderEventProducer;
import com.course.kafka.order.OrderStatus;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderEventProducer producer;

    @Test
    void createOrderPublishesCreatedEventWithOrderIdAsKey() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"Asha\",\"amount\":899.99}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("CREATED"));

        ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(producer).publish(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(OrderStatus.CREATED);
        assertThat(captor.getValue().orderId()).isNotBlank();
    }

    @Test
    void statusChangeUsesPathValues() throws Exception {
        mockMvc.perform(post("/api/orders/abc-1/status/SHIPPED"))
                .andExpect(status().isAccepted());

        ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(producer).publish(captor.capture());
        assertThat(captor.getValue().orderId()).isEqualTo("abc-1");
        assertThat(captor.getValue().status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void invalidRequestIsRejected() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(producer, never()).publish(any());
    }

    @Test
    void jsonSerializerRoundTrip() {
        OrderEvent event = new OrderEvent("e-1", "o-1", "Asha", OrderStatus.PAID,
                new BigDecimal("10.50"), Instant.parse("2026-10-09T05:00:00Z"));

        try (JsonSerializer<OrderEvent> serializer = new JsonSerializer<>();
             JsonDeserializer<OrderEvent> deserializer = new JsonDeserializer<>(OrderEvent.class)) {

            deserializer.addTrustedPackages("com.course.kafka.order");

            byte[] bytes = serializer.serialize("orders", event);
            assertThat(new String(bytes)).contains("\"status\":\"PAID\"");

            OrderEvent back = deserializer.deserialize("orders", bytes);
            assertThat(back).isEqualTo(event);
        }
    }
}
