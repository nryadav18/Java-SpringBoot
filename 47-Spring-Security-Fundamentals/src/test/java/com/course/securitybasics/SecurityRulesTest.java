package com.course.securitybasics;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/*
 * SecurityRulesTest - proves every rule in SecurityConfig with real HTTP-like calls.
 * MockMvc sends fake requests THROUGH the security filter chain (no real server needed).
 * httpBasic("user","pass") adds the "Authorization: Basic ..." header for us.
 *
 * Run: mvn test
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityRulesTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Public URL works without login")
    void publicUrlIsOpen() throws Exception {
        mockMvc.perform(get("/api/public/notice"))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("No credentials -> 401 Unauthorized")
    void anonymousGets401() throws Exception {
        mockMvc.perform(get("/api/me"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Wrong password -> 401 Unauthorized")
    void wrongPasswordGets401() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("student", "oops")))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Correct password -> 200 and my username")
    void studentCanSeeOwnProfile() throws Exception {
        mockMvc.perform(get("/api/me").with(httpBasic("student", "student123")))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("student"));
    }

    @Test
    @DisplayName("Logged in but not ADMIN -> 403 Forbidden")
    void studentGets403OnAdminUrl() throws Exception {
        mockMvc.perform(get("/api/admin/stats").with(httpBasic("student", "student123")))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN -> 200 on admin URL")
    void adminCanSeeStats() throws Exception {
        mockMvc.perform(get("/api/admin/stats").with(httpBasic("admin", "admin123")))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST to /api/** works without CSRF token (CSRF ignored for the API)")
    void postWithoutCsrfTokenWorksForApi() throws Exception {
        mockMvc.perform(post("/api/feedback").with(httpBasic("student", "student123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Great course!\"}"))
               .andExpect(status().isCreated());
    }
}
