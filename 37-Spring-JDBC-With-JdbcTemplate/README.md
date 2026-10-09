# 37 - Spring JDBC with JdbcTemplate

A Student REST API stored in an **H2** database using Spring JDBC. The `StudentRepository`
interface has **three implementations** so you can compare the APIs side by side.
Open `lesson.html` for the full lesson.

| File | What it shows |
|------|---------------|
| `resources/schema.sql`, `data.sql` | Table creation + sample rows, run automatically at startup |
| `resources/application.properties` | DataSource/HikariCP, `spring.sql.init.mode`, H2 console, SQL logging, `app.repository.type` |
| `repository/StudentRepository.java` | The contract |
| `repository/JdbcTemplateStudentRepository.java` | `JdbcTemplate`: query, queryForObject, update, KeyHolder, lambda RowMapper |
| `repository/NamedParameterStudentRepository.java` | `NamedParameterJdbcTemplate` with `:name` parameters, `MapSqlParameterSource` |
| `repository/JdbcClientStudentRepository.java` | `JdbcClient` fluent API (Spring 6.1+), automatic record mapping, `paramSource` |
| `repository/StudentRowMapper.java` | A reusable `RowMapper` |
| `config/StartupReport.java` | Prints the DataSource type, active repository, `queryForObject`/`queryForList` demos |
| `exception/ApiExceptionHandler.java` | ProblemDetail errors; `DuplicateKeyException` -> 409 |
| `service/`, `controller/`, `dto/`, `model/` | Layers from topic 33 |
| `src/test/.../*RepositoryTest.java` | One contract test run against all 3 implementations with `@JdbcTest` |
| `requests.http` | Sample requests |

## Run

```
mvn spring-boot:run                                                          # JdbcTemplate version
mvn spring-boot:run "-Dspring-boot.run.arguments=--app.repository.type=named"
mvn spring-boot:run "-Dspring-boot.run.arguments=--app.repository.type=jdbc-client"
mvn test
```

API: http://localhost:8080/api/students - H2 console: http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:studentdb`, user `sa`, empty password).
