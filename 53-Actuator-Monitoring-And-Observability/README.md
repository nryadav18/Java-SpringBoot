# 53 - Actuator, Monitoring and Observability

An "Order Service" that is easy to watch in production: health checks, app info, metrics,
Prometheus format and changing log levels at runtime. Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | `spring-boot-starter-actuator`, `micrometer-registry-prometheus`, `build-info` goal |
| `order/OrderService.java` | Custom Micrometer metrics: `Counter` (with tags), `Timer`, `Gauge`, `DistributionSummary` |
| `health/PaymentGatewayHealthIndicator.java` | Custom `HealthIndicator` |
| `health/PaymentGateway.java` + `admin/PaymentGatewayAdminController.java` | Fake external system you can switch down |
| `info/ShopInfoContributor.java` | Custom `InfoContributor` |
| `application.properties` | Safe endpoint exposure, health details, probes, info, metric tags |
| `application-dev.properties` | Exposes ALL endpoints (learning only) |
| `ActuatorEndpointsTest.java` | MockMvc tests for health, info, metrics, prometheus |
| `docker-compose.yml` + `monitoring/` | Optional Prometheus + Grafana |
| `requests.http` | All sample requests |

## Run
```
mvn spring-boot:run                                   # safe endpoints
mvn spring-boot:run -Dspring-boot.run.profiles=dev    # all endpoints (env, beans, mappings ...)
mvn test
docker compose up -d                                  # optional: Prometheus :9090, Grafana :3000 (admin/admin)
```
Start with http://localhost:8080/actuator and http://localhost:8080/actuator/health
