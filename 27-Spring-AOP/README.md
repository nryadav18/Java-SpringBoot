# 27 · Spring AOP (Aspect-Oriented Programming)

Add logging, timing and validation to many methods **without touching them**. Covers cross-cutting concerns,
AOP terms (aspect, advice, join point, pointcut, weaving), all advice types (`@Before`, `@After`,
`@AfterReturning`, `@AfterThrowing`, `@Around`), pointcut expressions, a custom `@LogExecutionTime`
annotation, JDK vs CGLIB proxies, and the self-invocation gotcha (important for `@Transactional`).
Open `lesson.html` in a browser for the full lesson.

## Files (`src/main/java/com/course/aop/`)
| File | What it shows |
|------|---------------|
| `App.java` | Demos 1-6: proxy types, normal call, exception, blocked call, timing, self-invocation |
| `config/AppConfig.java` | `@EnableAspectJAutoProxy` + component scan |
| `annotation/LogExecutionTime.java` | Custom annotation used as a pointcut marker |
| `aspect/CommonPointcuts.java` | Named, reusable `@Pointcut`s (execution, within, @annotation) |
| `aspect/LoggingAspect.java` | `@Before`, `@AfterReturning`, `@AfterThrowing`, `@After` |
| `aspect/PerformanceAspect.java` | `@Around` timing for `@LogExecutionTime` methods |
| `aspect/ValidationAspect.java` | `@Before` with `args(...)` binding that blocks bad calls |
| `service/OrderService.java` | Plain class -> CGLIB proxy; `placeBulkOrders` shows self-invocation |
| `service/BulkOrderService.java` | The self-invocation fix: call through another bean |
| `payment/PaymentService.java`, `CardPaymentService.java` | Interface-based bean -> JDK dynamic proxy |

`pom.xml`: `spring-context` + `spring-aspects` 6.2.11 (brings `aspectjweaver`).

## Run
```
cd "D:\Java-SpringBoot\27-Spring-AOP"
mvn -q compile exec:java
```
