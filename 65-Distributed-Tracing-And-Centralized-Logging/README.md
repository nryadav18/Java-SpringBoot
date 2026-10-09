# Topic 65 - Distributed Tracing & Centralized Logging

order-service calls payment-service; **Micrometer Tracing (Brave)** gives both the same trace id,
prints it in every log line and sends spans to **Zipkin**. Open `lesson.html` for the full lesson.

## Folder contents
| Path | What it shows |
|------|---------------|
| `order-service/` | Port **8081**. Traced RestClient (`PaymentClientConfig`), custom span `order.validate`, `CorrelationIdFilter` (baggage), Tracer API. |
| `payment-service/` | Port **8082**. Joins the trace automatically; custom span `payment.fraud-check`. |
| `docker-compose.yml` | Zipkin server (UI http://localhost:9411). |
| `requests.http` | Sample requests (incl. `correlation-id` and `traceparent` headers). |

## Start order
1. `docker compose up -d` (optional - without Zipkin the apps still run and log trace ids; only the UI is missing)
2. `cd payment-service` then `mvn spring-boot:run`
3. `cd order-service` then `mvn spring-boot:run`
4. `curl -X POST http://localhost:8081/api/orders -H "Content-Type: application/json" -d "{\"item\":\"Pizza\",\"amount\":450}"`
5. Compare the traceId in both consoles, then search it in Zipkin.

JSON log files for ELK/Loki: run with `-Dspring-boot.run.profiles=json-logs` (writes `logs/*.json`).
Stop: `Ctrl + C` in each terminal, then `docker compose down`.
