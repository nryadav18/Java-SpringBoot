# 58 - Event Streaming with Apache Kafka

Order events (CREATED, PAID, SHIPPED, CANCELLED) written to a Kafka topic with `KafkaTemplate` and read by two
consumer groups with `@KafkaListener`. JSON values, orderId keys (ordering), `NewTopic` beans,
`DefaultErrorHandler` with retries and a dead-letter topic. Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `docker-compose.yml` | one Kafka 3.9 broker in KRaft mode (no ZooKeeper) on `localhost:9092` + CLI tips |
| `src/main/resources/application.yml` | serializers, `acks=all`, idempotence, `auto-offset-reset`, ErrorHandlingDeserializer |
| `config/KafkaTopicConfig.java` | `NewTopic` beans: `orders` (3 partitions, 7-day retention) and `orders.DLT` |
| `config/KafkaErrorHandlingConfig.java` | `DefaultErrorHandler` + `DeadLetterPublishingRecoverer`, non-retryable exceptions |
| `order/OrderEvent.java`, `OrderStatus.java`, `OrderRequest.java` | the event record and request |
| `order/OrderEventProducer.java` | `kafkaTemplate.send(topic, key, value)` + callback with partition/offset |
| `order/OrderController.java` | REST endpoints that produce events |
| `consumer/NotificationConsumer.java` | group `notification-service`, `concurrency = "3"`, `ConsumerRecord` |
| `consumer/AnalyticsConsumer.java` | group `analytics-service`, `@Payload` / `@Header`, `GET /api/analytics` |
| `consumer/DeadLetterConsumer.java` | reads `orders.DLT` and its failure headers |
| `src/test/.../OrderControllerTest.java` | 4 tests without a broker (`@WebMvcTest` + JSON round trip) |
| `requests.http` | sample requests |

## Run
```
docker compose up -d        # start Kafka (wait ~15 s)
mvn spring-boot:run         # start the app, then use requests.http
docker compose down         # stop Kafka
```
