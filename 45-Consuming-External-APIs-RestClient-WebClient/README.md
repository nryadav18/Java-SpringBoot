# 45 - Consuming External APIs: RestTemplate, RestClient, WebClient, HTTP interfaces

Our Spring Boot app calls another API (https://jsonplaceholder.typicode.com) in four ways,
with timeouts and error handling. An `offline` profile uses a built-in mock, so it works without internet.
Open **lesson.html** for the full lesson.

## Files (under `src/main/java/com/course/apiclient`)
- `config/ExternalApiProperties.java` - base URL + timeouts from `application.properties`.
- `config/HttpClientsConfig.java` - builds RestTemplate, RestClient, WebClient and the HTTP-interface proxy.
- `client/PostHttpClient.java` - declarative `@HttpExchange` interface (no implementation class!).
- `service/LegacyPostService.java` - RestTemplate (classic).
- `service/PostService.java` - RestClient (recommended for blocking code).
- `service/ReactivePostService.java` - WebClient (reactive preview, parallel calls with `Mono.zip`).
- `web/DemoController.java` - `/api/demo/...` endpoints, one group per client + error demos.
- `web/ApiExceptionHandler.java` - remote 404 -> 404, remote error -> 502, timeout -> 503.
- `mock/MockJsonPlaceholderController.java` - fake JSONPlaceholder under `/mock-api` (+ slow/broken endpoints).
- `model/*` - `Post`, `User`, `NewPost`, `UserSummary` records.
- `src/test/.../PostServiceTest.java` - tests RestClient code with `MockRestServiceServer` (no network).
- `requests.http` - all sample requests.

## Run
```
mvn spring-boot:run                                         # real API (needs internet)
mvn spring-boot:run -Dspring-boot.run.profiles=offline      # built-in mock
mvn test                                                    # run the test
```
Then try http://localhost:8080/api/demo/rest-client/posts/1
