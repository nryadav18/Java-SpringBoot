# 46 - Testing Spring Boot Applications

A small Library **Book API** and, more importantly, a complete set of tests for it:
unit tests with Mockito, `@WebMvcTest` + MockMvc + `@MockitoBean`, `@DataJpaTest`, and a full
`@SpringBootTest` with `TestRestTemplate` on a random port. JaCoCo measures code coverage.
Open **lesson.html** for the full lesson.

## Files
- `pom.xml` - `spring-boot-starter-test` + JaCoCo plugin (report on `mvn test`, 70 % gate on `mvn verify`).
- `src/main/java/.../book/*` - entity, repository, service (business rules), controller, DTOs, exceptions.
- `src/main/java/.../common/GlobalExceptionHandler.java` - ProblemDetail errors (404, 409, 400).
- `src/main/java/.../config/DataSeeder.java` - sample books, switchable with `app.seed-data`.
- `src/test/java/.../book/BookServiceTest.java` - pure unit test: `@Mock`, `@InjectMocks`, `verify`, `ArgumentCaptor`, `@ParameterizedTest`.
- `src/test/java/.../book/BookControllerTest.java` - web slice: `@WebMvcTest`, `MockMvc`, `jsonPath`, `@MockitoBean`.
- `src/test/java/.../book/BookRepositoryTest.java` - JPA slice: `@DataJpaTest`, `TestEntityManager`.
- `src/test/java/.../BookApiIntegrationTest.java` - full app: `@SpringBootTest(RANDOM_PORT)`, `TestRestTemplate`.
- `requests.http` - sample requests to try the API manually.

## Run
```
mvn test                 # 30 tests; coverage report -> target/site/jacoco/index.html
mvn verify               # tests + fail if line coverage < 70 %
mvn test -Dtest=BookServiceTest   # run one test class
mvn spring-boot:run      # run the API itself
```
