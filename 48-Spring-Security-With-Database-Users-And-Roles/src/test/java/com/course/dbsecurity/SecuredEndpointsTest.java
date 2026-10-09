package com.course.dbsecurity;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

/*
 * SecuredEndpointsTest - full-stack tests through the real security filter chain.
 *
 * Three ways to "be somebody" in a test (all from spring-security-test):
 *   @WithMockUser(...)      -> a FAKE user, no database lookup. Fast. Principal is Spring's User class.
 *   @WithUserDetails("x")   -> loads REAL user "x" through our JpaUserDetailsService
 *                              (needed when the code expects OUR SecurityUser principal).
 *   .with(httpBasic(u, p))  -> sends a real Authorization header -> tests the real password check.
 *
 * Run: mvn test
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecuredEndpointsTest {

    @Autowired
    MockMvc mockMvc;

    // ---------- registration ----------

    @Test
    @DisplayName("Anyone can register -> 201, role USER, no password in response")
    void registerCreatesUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"password\":\"bob12345\"}"))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.username").value("bob"))
               .andExpect(jsonPath("$.roles[0]").value("USER"))
               .andExpect(jsonPath("$.password").doesNotExist());

        // ...and the new user can log in with the password (proves BCrypt hash was stored)
        mockMvc.perform(get("/api/me/context").with(httpBasic("bob", "bob12345")))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("bob"));
    }

    @Test
    @DisplayName("Duplicate username -> 409 Conflict")
    void duplicateUsername() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"whatever123\"}"))
               .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Too short password -> 400 Bad Request")
    void invalidRegistration() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"carl\",\"password\":\"123\"}"))
               .andExpect(status().isBadRequest());
    }

    // ---------- 401 / 403 / roles ----------

    @Test
    @DisplayName("No login -> 401")
    void anonymousGets401() throws Exception {
        mockMvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Seeded admin logs in with real password and lists users")
    void adminWithRealPassword() throws Exception {
        mockMvc.perform(get("/api/admin/users").with(httpBasic("admin", "admin123")))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    @DisplayName("USER on admin URL -> 403")
    void userCannotListUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "auditor", authorities = "REPORT_READ")
    @DisplayName("hasAuthority: a user with only the REPORT_READ permission can read reports")
    void permissionBasedAccess() throws Exception {
        mockMvc.perform(get("/api/reports/summary")).andExpect(status().isOk());
    }

    // ---------- method security with SpEL ----------

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    @DisplayName("Owner can read own notes")
    void ownerReadsOwnNotes() throws Exception {
        mockMvc.perform(get("/api/users/alice/notes")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    @DisplayName("Not the owner -> 403 from @PreAuthorize")
    void cannotReadOthersNotes() throws Exception {
        mockMvc.perform(get("/api/users/admin/notes")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "boss", roles = "ADMIN")
    @DisplayName("ADMIN can read anybody's notes")
    void adminReadsAnyNotes() throws Exception {
        mockMvc.perform(get("/api/users/alice/notes")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice", roles = "USER")
    @DisplayName("USER cannot delete users (@PreAuthorize on service) -> 403")
    void userCannotDelete() throws Exception {
        mockMvc.perform(delete("/api/admin/users/1"))
               .andExpect(status().isForbidden());
    }

    // ---------- current user ----------

    @Test
    @WithUserDetails("alice")   // real SecurityUser from the database -> @AuthenticationPrincipal works
    @DisplayName("@AuthenticationPrincipal gives our SecurityUser with id")
    void currentUser() throws Exception {
        mockMvc.perform(get("/api/me"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("alice"))
               .andExpect(jsonPath("$.id").isNumber());
    }
}
