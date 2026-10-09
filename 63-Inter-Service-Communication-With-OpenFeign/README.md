# Topic 63 - Inter-Service Communication with OpenFeign

order-service calls product-service through a **declarative OpenFeign client** (an interface - no HTTP code).
Open `lesson.html` for the full lesson.

## Folder contents
| Path | What it shows |
|------|---------------|
| `product-service/` | Plain REST API owning products (port **8081**). Has a `?delayMs=` option to simulate slowness. |
| `order-service/` | Places orders. `ProductClient` (@FeignClient), `ProductClientConfig` (ErrorDecoder + RequestInterceptor), timeouts + logging in `application.yml` (port **8082**). |
| `requests.http` | Sample requests for the VS Code REST Client extension. |

## Start order (two terminals)
1. `cd product-service` then `mvn spring-boot:run`  -> http://localhost:8081/api/products
2. `cd order-service` then `mvn spring-boot:run`   -> http://localhost:8082/api/orders/products

Try: `curl -X POST http://localhost:8082/api/orders -H "Content-Type: application/json" -d "{\"productId\":1,\"quantity\":2}"`

## Optional: with Eureka (load balancing)
Start `eureka-server` from topic 60 (port 8761), then run both services with
`mvn spring-boot:run -Dspring-boot.run.profiles=eureka`. A second product-service:
`mvn spring-boot:run -Dspring-boot.run.profiles=eureka -Dspring-boot.run.arguments=--server.port=9081`.

Stop each service with `Ctrl + C`.
