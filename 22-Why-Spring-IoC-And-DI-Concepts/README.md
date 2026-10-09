# 22 · Why Spring? IoC and DI Concepts

Your first contact with Spring. We take a tightly coupled program, fix it in **plain Java**
(interface + constructor injection + a hand-written assembler) and then let the **Spring IoC container**
do the same job with `@Configuration` + `@Bean`. Open `lesson.html` in a browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | Plain Spring project: only `spring-context` 6.2.11 + `exec-maven-plugin` to run `main()` |
| `step1/EmailSender.java`, `step1/TightOrderService.java` | The problem: `new` inside the class = tight coupling |
| `notification/NotificationService.java` | The interface (the contract) |
| `notification/EmailNotificationService.java`, `SmsNotificationService.java` | Two interchangeable implementations |
| `order/OrderService.java` | Constructor injection - plain Java, no Spring code |
| `step2/ManualAssembler.java` | Hand-made "container": decides, creates, wires (IoC by hand) |
| `step3/AppConfig.java` | The Spring way: `@Configuration` + `@Bean` |
| `App.java` | Runs step 1, 2 and 3 one after another |
| `src/test/.../OrderServiceTest.java` | DI makes testing easy: a fake notifier, no Spring needed |

## Run
```
cd "D:\Java-SpringBoot\22-Why-Spring-IoC-And-DI-Concepts"
mvn -q compile exec:java
mvn -q test
```
