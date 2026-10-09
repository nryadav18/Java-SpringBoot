# 49 - JWT Authentication

A stateless "Food Delivery" API: log in once, receive a signed JSON Web Token (JJWT 0.12.6),
then send `Authorization: Bearer <token>` on every call. Users live in H2 via JPA. Open **lesson.html**.

## Files (under `src/main/java/com/course/jwtauth`)
| File | What it shows |
|------|---------------|
| `security/JwtProperties.java` | `app.jwt.secret / expiration / issuer` bound to a record |
| `security/JwtService.java` | Generate, parse, validate tokens (HS256) |
| `security/JwtAuthenticationFilter.java` | `OncePerRequestFilter` reading the Bearer header |
| `security/SecurityConfig.java` | Stateless chain, CSRF off, custom 401, filter placement, `AuthenticationManager` |
| `security/AppUserDetailsService.java` | Loads users from the database |
| `auth/AuthController.java`, `auth/AuthService.java` | `POST /api/auth/register`, `POST /api/auth/login` |
| `web/OrderController.java` | Protected endpoints `/api/hello`, `/api/orders`, `/api/admin/dashboard` |
| `web/ApiExceptionHandler.java` | 401 for bad login, 409 for duplicate username |
| `config/DataSeeder.java` | `admin/admin123` (ADMIN), `alice/alice123` (USER) |
| `src/test/...` | `JwtServiceTest` (unit) + `AuthFlowTest` (login -> token -> call) |
| `requests.http` | Full flow; reuses the token automatically |

## Run
```
mvn spring-boot:run
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"alice\",\"password\":\"alice123\"}"
curl http://localhost:8080/api/hello -H "Authorization: Bearer <paste token>"
mvn test
```
Set your own secret with the `JWT_SECRET` environment variable (Base64, 32+ random bytes).
