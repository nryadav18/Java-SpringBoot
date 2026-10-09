package com.course.jwtauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * AuthFlowTest - the complete story: login -> token -> protected call.
 * Requests travel through the REAL filter chain, including our JwtAuthenticationFilter.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    /** Helper: log in and return the accessToken string. */
    private String loginAndGetToken(String username, String password) throws Exception {
        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        return node.get("accessToken").asText();
    }

    @Test
    void loginThenCallProtectedEndpoint() throws Exception {
        String token = loginAndGetToken("alice", "alice123");
        assertThat(token).startsWith("eyJ");   // Base64URL of '{"' - every JWT starts like this

        mockMvc.perform(get("/api/hello").header("Authorization", "Bearer " + token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void registerReturnsATokenThatWorks() throws Exception {
        String json = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"password\":\"bob12345\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(json).get("accessToken").asText();

        mockMvc.perform(get("/api/orders").header("Authorization", "Bearer " + token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.customer").value("bob"));
    }

    @Test
    void wrongPasswordGives401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"WRONG\"}"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void noTokenGives401() throws Exception {
        mockMvc.perform(get("/api/hello"))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void garbageTokenGives401() throws Exception {
        mockMvc.perform(get("/api/hello").header("Authorization", "Bearer not.a.realtoken"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void userTokenOnAdminUrlGives403() throws Exception {
        String token = loginAndGetToken("alice", "alice123");
        mockMvc.perform(get("/api/admin/dashboard").header("Authorization", "Bearer " + token))
               .andExpect(status().isForbidden());
    }

    @Test
    void adminTokenOnAdminUrlGives200() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/admin/dashboard").header("Authorization", "Bearer " + token))
               .andExpect(status().isOk());
    }

    /*
     * You do NOT always need a real token in tests: @WithMockUser fills the SecurityContext
     * directly, skipping login. Use it when you test controller logic, not the JWT mechanics.
     */
    @Test
    @WithMockUser(username = "tester", roles = "ADMIN")
    void withMockUserSkipsTheTokenEntirely() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")).andExpect(status().isOk());
    }
}
