# 70 - Design Patterns, Clean Code & Best Practices

A console Spring Boot app: one runnable demo per SOLID principle and per design pattern
(with "bad vs good" code), plus a clean-code before/after. Open `lesson.html` for the lesson.

## Files (`src/main/java/com/course/patterns/...`)
| Package / file | What it shows |
|----------------|---------------|
| `demo/PatternDemo`, `demo/DemoRunner` | Every demo is a bean; a `CommandLineRunner` runs the `List<PatternDemo>` in `@Order` |
| `solid/srp/SrpDemo` | Single Responsibility: invoice calc / format / store split |
| `solid/ocp/OcpDemo` | Open/Closed: `Discount` interface instead of if/else |
| `solid/lsp/LspDemo` | Liskov: Penguin vs `FlyingBird` |
| `solid/isp/IspDemo` | Interface Segregation: Printer / Scanner / Fax |
| `solid/dip/DipDemo` | Dependency Inversion: service depends on `OrderRepository` interface |
| `patterns/singleton/SingletonDemo` | Enum singleton + Spring singleton scope |
| `patterns/factory/FactoryDemo` | `NotificationFactory` with a switch expression |
| `patterns/builder/Pizza`, `BuilderDemo` | Immutable object with a fluent builder + validation |
| `patterns/adapter/AdapterDemo` | Legacy bank SDK adapted to `PaymentGateway` |
| `patterns/strategy/*` | Payment strategies as beans, chosen from `Map<String, PaymentStrategy>` |
| `patterns/observer/*` | `ApplicationEventPublisher` + `@EventListener` |
| `patterns/templatemethod/TemplateMethodDemo` | Report template (the idea behind `JdbcTemplate`) |
| `patterns/proxy/ProxyDemo` | JDK dynamic proxy (timing) + Spring `ProxyFactory` (caching) |
| `cleancode/CleanCodeDemo` | Same pricing logic before/after clean-code refactoring |
| `src/test/...` | 12 tests: builder, refactoring safety net, Spring strategy wiring |

## Run
```powershell
mvn spring-boot:run     # prints all 14 demos and exits
mvn test                # 12 tests
```
