# 57 - Messaging with RabbitMQ

"Order placed -> notification" using Spring AMQP (`spring-boot-starter-amqp`): topic, direct and fanout
exchanges, JSON messages, `@RabbitListener`, retries and a dead-letter queue. Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `docker-compose.yml` | RabbitMQ 3 with the management UI (http://localhost:15672, guest/guest) |
| `application.properties` | connection, listener retry settings, queue/exchange names |
| `config/RabbitConfig.java` | exchanges, queues, bindings, dead-letter setup, JSON `MessageConverter` |
| `order/OrderController.java` | `POST /api/orders` (202 Accepted), cancel, announcements |
| `order/OrderEventPublisher.java` | `RabbitTemplate.convertAndSend(exchange, routingKey, event)` |
| `order/OrderPlacedEvent.java`, `OrderCancelledEvent.java`, `OrderRequest.java` | message + request records |
| `notification/NotificationListener.java` | `@RabbitListener`; fails for `@fail.test` emails to show retries + DLQ |
| `notification/AnalyticsListener.java` | class-level `@RabbitListener` + `@RabbitHandler` per message type |
| `notification/DeadLetterListener.java` | reads the dead-letter queue (raw `Message`, `x-death` header) |
| `notification/AnnouncementListener.java` | fanout exchange consumer |
| `src/test/.../OrderControllerTest.java` | tests that run without RabbitMQ (`@WebMvcTest` + converter round trip) |
| `requests.http` | sample requests |

## Run
```
docker compose up -d        # start RabbitMQ
mvn spring-boot:run         # start the app, then send the requests in requests.http
docker compose down         # stop RabbitMQ when done
```
