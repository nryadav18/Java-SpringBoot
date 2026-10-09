package com.course.resourceserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

/*
 * ResourceServerTest - runs WITHOUT Keycloak.
 * jwt() from spring-security-test skips signature checking and directly puts a
 * JwtAuthenticationToken into the SecurityContext, as if a valid token had arrived.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ResourceServerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void publicEndpointNeedsNoToken() throws Exception {
        mockMvc.perform(get("/api/public/courses")).andExpect(status().isOk());
    }

    @Test
    void noTokenGives401() throws Exception {
        mockMvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void anyValidTokenCanCallMe() throws Exception {
        mockMvc.perform(get("/api/me").with(jwt().jwt(j -> j
                        .subject("1b7c-uuid")
                        .claim("preferred_username", "alice")
                        .claim("email", "alice@example.com"))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void profileNeedsProfileScope() throws Exception {
        mockMvc.perform(get("/api/profile").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_email"))))
               .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/profile").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_profile"))))
               .andExpect(status().isOk());
    }

    @Test
    void adminReportNeedsAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin/report").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
               .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/report").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
               .andExpect(status().isOk());
    }

    /** Unit test of the converter with a Keycloak-shaped token. */
    @Test
    void converterReadsScopesAndRealmRoles() {
        Jwt keycloakToken = Jwt.withTokenValue("dummy")
                .header("alg", "RS256")
                .claim("scope", "openid profile email")
                .claim("realm_access", Map.of("roles", List.of("user", "admin")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        List<String> authorities = new KeycloakRealmRoleConverter().convert(keycloakToken).stream()
                .map(Object::toString).toList();

        assertThat(authorities).containsExactlyInAnyOrder(
                "SCOPE_openid", "SCOPE_profile", "SCOPE_email", "ROLE_USER", "ROLE_ADMIN");
    }
}
