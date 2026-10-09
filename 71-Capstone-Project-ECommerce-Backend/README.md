# 71 · Capstone — ShopEasy E-Commerce Backend

An enterprise-style REST backend for an online shop: JWT login with roles, product catalog with search and
pagination, cart, transactional checkout with stock checks, caching, Flyway, OpenAPI, Actuator, tests, Docker and CI.
**Read `lesson.html` first** — it is the full project guide.

| Path | What it is |
|------|------------|
| `src/main/java/com/course/shopeasy/` | Package-by-feature: `auth`, `user`, `category`, `product`, `cart`, `order`, plus `security`, `config`, `common` |
| `src/main/resources/application.yml` | Default config (H2 in-memory, JWT, actuator, springdoc, logging) |
| `src/main/resources/application-mysql.yml` | `mysql` profile |
| `src/main/resources/db/migration/` | Flyway: `V1__create_schema.sql`, `V2__seed_catalog.sql` |
| `src/test/java/...` | Unit (Mockito), `@WebMvcTest`, `@SpringBootTest` end-to-end tests |
| `requests.http` | The whole API flow for the VS Code REST Client |
| `Dockerfile`, `docker-compose.yml` | Container image; app + MySQL 8.4 |
| `.github/workflows/ci.yml` | GitHub Actions: build, test, Docker image |

## Run

```bash
mvn spring-boot:run                     # H2, http://localhost:8080
mvn test                                # all tests
docker compose up -d mysql              # MySQL only, then:
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
docker compose up --build               # app + MySQL in containers
```

- Swagger UI: http://localhost:8080/swagger-ui.html (click **Authorize**, paste the `accessToken`)
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:shopeasy`, user `sa`)
- Demo users: `admin@shopeasy.com / Admin@123`, `customer@shopeasy.com / Customer@123`
