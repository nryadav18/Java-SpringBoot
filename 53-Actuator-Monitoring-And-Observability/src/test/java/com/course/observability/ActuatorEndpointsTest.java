package com.course.observability;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/*
 * Checks that our custom health indicator, info contributor and metrics are visible
 * through the actuator endpoints.   Run with:  mvn test
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability   // tests switch metric EXPORTERS (e.g. /actuator/prometheus) off by default - switch them on
@DirtiesContext   // the tests change shared state (gateway up/down) - start a fresh context afterwards
class ActuatorEndpointsTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void healthIsUpAndShowsOurPaymentGateway() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.paymentGateway.details.provider").value("DemoPay"));
    }

    @Test
    void healthIsDownWhenTheGatewayIsDown() throws Exception {
        mockMvc.perform(post("/api/admin/payment-gateway").param("up", "false"));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable())             // HTTP 503
                .andExpect(jsonPath("$.status").value("DOWN"));

        mockMvc.perform(post("/api/admin/payment-gateway").param("up", "true"));
    }

    @Test
    void placingAnOrderIncrementsTheCustomCounter() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"product\":\"Coffee Mug\",\"amount\":12.50,\"paymentMethod\":\"UPI\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/actuator/metrics/shop.orders.placed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements[0].value").isNumber());

        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("shop_orders_placed_total")));
    }

    @Test
    void infoContainsOurShopSection() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("Order Service"))
                .andExpect(jsonPath("$.shop.region").value("EU-West"));
    }
}
