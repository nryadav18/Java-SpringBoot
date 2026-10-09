package com.course.oauth2login;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/*
 * OAuth2LoginTest - we cannot click "Login with GitHub" in a unit test (and should not call GitHub).
 * spring-security-test fakes the RESULT of a successful login:
 *   oauth2Login() -> a logged-in OAuth2User (like GitHub)
 *   oidcLogin()   -> a logged-in OidcUser with an ID token (like Google/Keycloak)
 */
@SpringBootTest
@AutoConfigureMockMvc
class OAuth2LoginTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void homePageIsPublic() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void anonymousUserIsSentToLogin() throws Exception {
        // Two providers configured (github + google) -> Spring's provider-choice page at /login
        mockMvc.perform(get("/me"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void githubStyleLogin() throws Exception {
        mockMvc.perform(get("/me").with(oauth2Login()
                        .attributes(attrs -> {
                            attrs.put("sub", "583231");        // default name attribute of the mock
                            attrs.put("login", "octocat");
                        })))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.attributes.login").value("octocat"))
               .andExpect(jsonPath("$.loginType").value(org.hamcrest.Matchers.startsWith("plain OAuth2")));
    }

    @Test
    void openIdConnectLogin() throws Exception {
        mockMvc.perform(get("/me").with(oidcLogin()
                        .idToken(token -> token.claim("email", "alice@example.com"))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.idTokenClaims.email").value("alice@example.com"))
               .andExpect(jsonPath("$.loginType").value(org.hamcrest.Matchers.startsWith("OpenID Connect")));
    }
}
