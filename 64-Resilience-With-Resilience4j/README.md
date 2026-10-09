# Topic 64 - Resilience with Resilience4j

store-service calls inventory-service and protects itself with a **circuit breaker, retry, time limiter,
rate limiter and bulkhead** (Resilience4j 2.2.0 annotations). Open `lesson.html` for the full lesson.

## Folder contents
| Path | What it shows |
|------|---------------|
| `inventory-service/` | The downstream (port **8083**) with a chaos switch: `POST /api/inventory/chaos?mode=FAIL\|SLOW\|OK`. |
| `store-service/` | The caller (port **8084**). `StockService` has the annotations + fallbacks; `application.yml` has all thresholds; actuator shows state. |
| `requests.http` | Step-by-step demo requests (VS Code REST Client). |

## Start order (two terminals)
1. `cd inventory-service` then `mvn spring-boot:run`
2. `cd store-service` then `mvn spring-boot:run`

store-service also starts WITHOUT inventory-service - every stock call then returns a fallback.

## Quick demo
```
curl http://localhost:8084/api/store/stock/LAPTOP-1                    # real data
curl -X POST "http://localhost:8083/api/inventory/chaos?mode=FAIL"      # break the downstream
curl http://localhost:8084/api/store/stock/LAPTOP-1                    # retries, then fallback; circuit opens
curl http://localhost:8084/actuator/circuitbreakers                    # state: OPEN
```
Stop each service with `Ctrl + C`.
