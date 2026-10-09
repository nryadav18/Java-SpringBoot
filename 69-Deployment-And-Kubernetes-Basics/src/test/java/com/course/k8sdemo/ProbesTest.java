package com.course.k8sdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/*
 * ProbesTest.java - checks the endpoints Kubernetes depends on.
 * If someone breaks the probes, pods would never become ready - better to find out in CI!
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // we change the readiness state, so give the next test class a fresh context
class ProbesTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void livenessAndReadinessAreUp() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void readinessCanBeSwitchedOffAndOn() throws Exception {
        mockMvc.perform(post("/ops/readiness/off")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isServiceUnavailable())                 // 503 -> pod gets no traffic
                .andExpect(jsonPath("$.status").value("OUT_OF_SERVICE"));

        mockMvc.perform(post("/ops/readiness/on")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
    }

    @Test
    void infoNeverShowsTheRealSecret() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentApiKey").value("****-key"));
    }
}
