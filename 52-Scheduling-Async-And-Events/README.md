# 52 - Scheduling, Async and Events

Background work in Spring Boot: timed jobs (`@Scheduled`), work on other threads (`@Async`),
and decoupled reactions to things that happened (application events). Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `config/SchedulingConfig.java` | `@EnableScheduling`, switchable with `app.scheduling.enabled` |
| `config/AsyncConfig.java` | `@EnableAsync`, custom `ThreadPoolTaskExecutor`, uncaught-exception handler |
| `jobs/ShopJobs.java` | `@Scheduled` with `fixedRate`, `fixedDelay` and `cron` (values from properties) |
| `user/UserService.java` | `@Transactional` registration that publishes `UserRegisteredEvent` |
| `user/UserRegisteredEvent.java` | The event (a record) |
| `notification/WelcomeEmailListener.java` | `@Async` + `@TransactionalEventListener(AFTER_COMMIT)` |
| `notification/AuditLogListener.java` | Synchronous `@EventListener`, `@Order`, `condition` |
| `notification/RegistrationFailedListener.java` | `AFTER_ROLLBACK` listener |
| `notification/StartupListener.java` | Listening to Spring's own `ApplicationReadyEvent` |
| `report/ReportService.java` + `DashboardController.java` | `@Async` returning `CompletableFuture`, combined with `allOf` |
| `EventsAndAsyncTest.java` | `@RecordApplicationEvents` test + parallel async test |
| `requests.http` | Sample requests |

## Run
```
mvn spring-boot:run
mvn test
```
Then send the requests in `requests.http` and watch the thread names in the log
(`[nio-8080-exec-1]`, `[async-1]`, `[sched-1]`).
