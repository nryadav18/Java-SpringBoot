package com.course.cicd.shipping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/*
 * ShippingControllerTest.java - a web-layer test with MockMvc (topic 46).
 * @WebMvcTest starts only the MVC part of Spring (no real server, no port),
 * so it is fast enough to run in every CI build.
 * We @Import the real ShippingService because it has no dependencies of its own.
 */
@WebMvcTest(ShippingController.class)
@Import(ShippingService.class)
class ShippingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsQuoteAsJson() throws Exception {
        mockMvc.perform(get("/api/shipping/quote")
                        .param("weightKg", "2.5")
                        .param("express", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightKg").value(2.5))
                .andExpect(jsonPath("$.express").value(true))
                .andExpect(jsonPath("$.cost").value(15.00));
    }

    @Test
    void expressIsOptional() throws Exception {
        mockMvc.perform(get("/api/shipping/quote").param("weightKg", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.express").value(false))
                .andExpect(jsonPath("$.cost").value(7.00));
    }

    @Test
    void tooHeavyGives400() throws Exception {
        mockMvc.perform(get("/api/shipping/quote").param("weightKg", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists());
    }
}
