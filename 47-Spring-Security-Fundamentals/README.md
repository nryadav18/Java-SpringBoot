# 47 - Spring Security Fundamentals

Protect a small "College Portal" REST API with Spring Security: in-memory users, BCrypt,
HTTP Basic + form login, `permitAll` / `authenticated` / `hasRole`, CSRF, CORS, and 401 vs 403.
Open **lesson.html** for the full explanation.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | `spring-boot-starter-security` + `spring-security-test` |
| `config/SecurityConfig.java` | `SecurityFilterChain` (Lambda DSL), in-memory users, BCrypt, CSRF, CORS |
| `config/PasswordHashingDemo.java` | Prints BCrypt hashes at startup (same password, different hash) |
| `web/PublicController.java` | `/api/public/notice` - open to everyone |
| `web/StudentController.java` | `/api/me`, `POST /api/feedback` - any logged-in user |
| `web/AdminController.java` | `/api/admin/stats` - ADMIN only (403 for USER) |
| `web/HomeController.java` | `/` HTML page to try form login in the browser |
| `src/test/.../SecurityRulesTest.java` | MockMvc tests for 200 / 401 / 403 |
| `requests.http` | Ready-made requests for the VS Code REST Client |

Users: `student / student123` (USER), `admin / admin123` (USER, ADMIN)

## Run
```
mvn spring-boot:run
curl -i -u student:student123 http://localhost:8080/api/me
mvn test
```
