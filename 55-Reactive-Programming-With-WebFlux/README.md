# 55 - Reactive Programming with WebFlux

Non-blocking web apps with Spring WebFlux, Project Reactor (Mono/Flux), Server-Sent Events,
WebClient and R2DBC (reactive database access with H2). Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | webflux + data-r2dbc + r2dbc-h2 + reactor-test |
| `src/main/resources/application.properties` | R2DBC URL (`r2dbc:h2:mem:...`), WebClient base URL |
| `src/main/resources/schema.sql`, `data.sql` | products table + sample rows |
| `playground/ReactorPlayground.java` | Mono/Flux operators in a plain `main()` (map, filter, flatMap, zip, onErrorResume, delayElements, backpressure) |
| `product/*` | R2DBC entity, `ReactiveCrudRepository`, service, annotated `@RestController` |
| `stream/PriceStreamController.java` | Server-Sent Events endpoint (`text/event-stream`) |
| `functional/RouterConfig.java`, `GreetingHandler.java` | functional routes (`RouterFunction`) |
| `client/*` | `WebClient` calling the app's own API, with timeout + fallback |
| `src/test/.../ReactorOperatorsTest.java` | StepVerifier tests (incl. virtual time) |
| `src/test/.../ProductApiTest.java` | WebTestClient + StepVerifier integration test |
| `requests.http` | sample requests for the VS Code REST Client extension |

## Run
```
mvn spring-boot:run          # http://localhost:8080/api/products
curl -N http://localhost:8080/api/stream/prices
mvn test                     # 11 tests
```
