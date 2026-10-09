package com.course.oauth2login;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * ProfileController - what do we know about the user after "Login with ..."?
 *
 *   OAuth2User  -> plain OAuth2 provider (GitHub). Spring called GitHub's "user-info" API
 *                  with the access token and got attributes like login, name, avatar_url.
 *   OidcUser    -> OpenID Connect provider (Google, Keycloak). PLUS an ID TOKEN: a signed JWT
 *                  that directly says WHO the user is (sub, email, name...). OidcUser extends OAuth2User.
 */
@RestController
public class ProfileController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return """
                <html><body style="font-family:sans-serif;max-width:640px;margin:40px auto">
                <h1>OAuth2 Login demo</h1>
                <p>Choose how to log in. This app never sees your password.</p>
                <ul>
                  <li><a href="/oauth2/authorization/github">Login with GitHub</a></li>
                  <li><a href="/oauth2/authorization/google">Login with Google</a></li>
                  <li><a href="/oauth2/authorization/keycloak">Login with local Keycloak</a>
                      (only with the <code>keycloak</code> profile)</li>
                </ul>
                <p>After login: <a href="/me">/me</a> and <a href="/me/token">/me/token</a></p>
                </body></html>
                """;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal OAuth2User user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", user.getName());                 // GitHub: the numeric id; OIDC: "sub"
        result.put("authorities", user.getAuthorities().stream().map(Object::toString).sorted().toList());
        result.put("attributes", user.getAttributes());     // everything the provider told us

        // Pattern matching for instanceof (Java 16+): is this an OpenID Connect login?
        if (user instanceof OidcUser oidcUser) {
            result.put("loginType", "OpenID Connect (has an ID token)");
            result.put("idTokenClaims", oidcUser.getIdToken().getClaims());
        } else {
            result.put("loginType", "plain OAuth2 (no ID token, attributes came from the user-info API)");
        }
        return result;
    }

    /*
     * The ACCESS TOKEN our app received. With it our app could call the provider's API
     * on behalf of the user (e.g. list the user's GitHub repositories).
     * We show only safe metadata - NEVER print real tokens in logs or pages.
     */
    @GetMapping("/me/token")
    public Map<String, Object> token(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("provider", client.getClientRegistration().getRegistrationId());
        result.put("tokenType", client.getAccessToken().getTokenType().getValue());
        result.put("scopes", client.getAccessToken().getScopes());
        result.put("issuedAt", client.getAccessToken().getIssuedAt());
        result.put("expiresAt", client.getAccessToken().getExpiresAt());
        result.put("hasRefreshToken", client.getRefreshToken() != null);
        return result;
    }
}
