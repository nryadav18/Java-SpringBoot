# 30 - Logging in Spring Boot

Proper logging with SLF4J + Logback: log levels, one logger per class, `{}` placeholders,
per-package levels, rolling log files with `logback-spring.xml`, a request id in every line via MDC,
and what NOT to log (passwords, card numbers, personal data).
Open **lesson.html** for the full explanation.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | Only the web starter - logging (SLF4J + Logback) comes with it |
| `src/main/java/com/course/loggingdemo/LoggingDemoApplication.java` | Main class + a startup INFO log |
| `src/main/java/com/course/loggingdemo/OrderService.java` | All levels, parameterized logs, exception logging, masking |
| `src/main/java/com/course/loggingdemo/OrderController.java` | Endpoints that trigger each kind of log, `isDebugEnabled()` |
| `src/main/java/com/course/loggingdemo/RequestIdFilter.java` | MDC request id for every request (+ `X-Request-Id` header) |
| `src/main/java/com/course/loggingdemo/Order.java` | Simple record |
| `src/main/resources/application.properties` | `logging.level.*`, log groups, file properties |
| `src/main/resources/logback-spring.xml` | Console + rolling file appenders, pattern with `%X{requestId}`, `<springProfile>` |
| `src/test/java/.../OrderServiceTest.java` | Tests that check log output (and that card numbers never appear) |
| `requests.http` | Sample requests |

## Run
```
mvn spring-boot:run                       # logs to console and logs/food-delivery.log
mvn spring-boot:run -Dspring-boot.run.profiles=prod   # our package at INFO only
mvn test
```
