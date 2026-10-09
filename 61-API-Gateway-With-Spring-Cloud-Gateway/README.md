# 61 · API Gateway with Spring Cloud Gateway

One public entry point (**http://localhost:8080**) that routes requests to the right
microservice (found through Eureka) and handles cross-cutting concerns once.
Read **`lesson.html`** first.

| Folder             | Port | What it shows |
|--------------------|------|---------------|
| `eureka-server/`   | 8761 | Service registry (same as Topic 60) |
| `api-gateway/`     | 8080 | `spring-cloud-starter-gateway-server-webflux`; routes in `application.yml` (Path predicates, `lb://`, AddRequestHeader, AddResponseHeader, RewritePath, StripPrefix, discovery locator); `LoggingGlobalFilter` (correlation id + timing); `ApiKeyGlobalFilter` (POST /api/orders needs `X-API-KEY`) |
| `product-service/` | 8081 | Product API + `/api/products/headers` to see headers added by the gateway |
| `order-service/`   | 8082 | Minimal order API; calls product-service by name |
| `requests.http`    | –    | Sample requests (VS Code REST Client) |

## Start order (one terminal per app)

```
cd eureka-server   ; mvn spring-boot:run     # 1st
cd product-service ; mvn spring-boot:run     # 2nd
cd order-service   ; mvn spring-boot:run     # 3rd
cd api-gateway     ; mvn spring-boot:run     # LAST
```

Wait ~10–15 s after everything is up (Eureka registration), then use only port **8080**:
`GET http://localhost:8080/api/products` · `GET http://localhost:8080/shop/products/1` ·
`POST http://localhost:8080/api/orders` with header `X-API-KEY: course-secret-123`.
All routes: `GET http://localhost:8080/actuator/gateway/routes`. Stop each app with **Ctrl+C**.
