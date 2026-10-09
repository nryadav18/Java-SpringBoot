# 25 · Bean Scopes & Lifecycle

How many objects Spring creates for a bean (**scopes**: singleton, prototype, request, session) and
the exact steps of a bean's life from constructor to destruction (**lifecycle**): `@PostConstruct`,
`InitializingBean`, init/destroy methods, `@PreDestroy`, `DisposableBean`, `BeanPostProcessor`, `@Lazy`,
and the "prototype inside a singleton" trap fixed with `ObjectProvider`. Domain: an online shop.
Open `lesson.html` in a browser for the full lesson.

## Files (`src/main/java/com/course/scopes/`)
| File | What it shows |
|------|---------------|
| `App.java` | Demos 1-5 with numbered lifecycle output |
| `config/AppConfig.java` | `@Bean(initMethod = ..., destroyMethod = ...)` |
| `catalog/ProductCatalog.java` | Singleton (default scope) |
| `cart/ShoppingCart.java` | `@Scope("prototype")`, `@PostConstruct` runs, `@PreDestroy` never does |
| `cart/BrokenCheckoutService.java` | The trap: prototype injected into a singleton = one shared cart |
| `cart/CheckoutService.java` | The fix: `ObjectProvider<ShoppingCart>` |
| `lifecycle/DatabaseConnectionPool.java` | Every lifecycle callback, numbered 1-11 |
| `lifecycle/PoolSettings.java` | Small record injected by setter injection |
| `lifecycle/LoggingBeanPostProcessor.java` | A `BeanPostProcessor` that logs before/after initialisation |
| `report/SalesReportService.java` | `@Lazy` singleton created on first use |

`pom.xml` adds `jakarta.annotation-api` 2.1.1 for `@PostConstruct` / `@PreDestroy`.

## Run
```
cd "D:\Java-SpringBoot\25-Bean-Scopes-And-Lifecycle"
mvn -q compile exec:java
```
