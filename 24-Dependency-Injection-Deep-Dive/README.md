# 24 · Dependency Injection Deep Dive

Everything about *how* Spring injects dependencies, using a "QuickBite" food-delivery demo:
constructor vs setter vs field injection, `@Autowired`, several implementations (`@Primary`, `@Qualifier`),
injecting a `List`/`Map` of beans, `@Value` + `@PropertySource`, optional dependencies and circular dependencies.
Open `lesson.html` in a browser for the full lesson.

## Files (`src/main/java/com/course/...`)
| File | What it shows |
|------|---------------|
| `di/App.java` | Runs demos 1-6 and prints the results |
| `di/config/AppConfig.java` | `@ComponentScan`, `@PropertySource`, static `PropertySourcesPlaceholderConfigurer` |
| `di/config/ShopSettings.java` | `@Value("${...}")` with defaults, type conversion (double, int, String[]) |
| `di/payment/PaymentGateway.java` + `CardPayment` / `UpiPayment` / `PayPalPayment` | 3 implementations, `@Primary`, `@Order`, custom bean name |
| `di/payment/PaymentRegistry.java` | Injecting `List<PaymentGateway>` and `Map<String, PaymentGateway>` |
| `di/order/CheckoutService.java` | Constructor injection (no `@Autowired` needed), gets the `@Primary` bean |
| `di/order/ExpressCheckoutService.java` | `@Qualifier("upiPayment")` |
| `di/order/ReceiptService.java` / `LegacyAuditService.java` | Setter injection / field injection (avoid) |
| `di/notify/OrderNotifier.java` | Optional dependencies: `Optional`, `ObjectProvider`, `@Autowired(required = false)` |
| `circular/broken/*` | Circular dependency -> `BeanCurrentlyInCreationException` |
| `circular/fixed/*` | The fix: extract the shared part into a third bean |
| `src/main/resources/application.properties` | Shop settings read by `@Value` |

## Run
```
cd "D:\Java-SpringBoot\24-Dependency-Injection-Deep-Dive"
mvn -q compile exec:java
```
(Demo 6 makes Spring print a `WARNING` line about the circular reference - that is expected.)
