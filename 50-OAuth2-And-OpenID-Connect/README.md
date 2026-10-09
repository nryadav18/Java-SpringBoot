# 50 - OAuth2 and OpenID Connect

Two small Spring Boot projects + a local Keycloak. Open **lesson.html** for the full explanation.

| Folder / file | What it shows |
|---------------|---------------|
| `oauth2-login-client/` | OAuth2 **client**: "Login with GitHub / Google" (`oauth2Login()`), OIDC `OidcUser`, ID token, `@RegisteredOAuth2AuthorizedClient`. Profile `keycloak` logs in against the local Keycloak. Port 8080. |
| `resource-server/` | OAuth2 **resource server**: validates Bearer JWTs from an issuer (Keycloak/Auth0/Okta), scopes and Keycloak realm roles -> authorities. Port 8081. |
| `docker-compose.yml` | Starts Keycloak 26 on port 8180 and imports the realm |
| `keycloak/realm-course.json` | Realm `course`: users `alice/alice123`, `boss/boss123` (admin), client `course-client/course-secret` |
| `requests.http` | Get tokens from Keycloak and call the resource server |

## Start order
1. `docker compose up -d` (needs Docker Desktop running) - wait until http://localhost:8180 answers.
2. Resource server: `cd resource-server` then `mvn spring-boot:run`.
3. Login client against Keycloak: `cd oauth2-login-client` then `mvn spring-boot:run "-Dspring-boot.run.profiles=keycloak"` and open http://localhost:8080.
4. Login with GitHub instead: create a GitHub OAuth App (lesson section 5), set `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET`, run without the profile.

Both projects compile and their tests pass offline (`mvn test`) - real logins need Keycloak or real GitHub/Google credentials.
