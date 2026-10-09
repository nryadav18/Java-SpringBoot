# 35 - Global Exception Handling

A small Library (Book) API where every error becomes a clean, consistent JSON response.
Open `lesson.html` for the full lesson.

| File | What it shows |
|------|---------------|
| `exception/ResourceNotFoundException.java` | Reusable 404 exception (+ `@ResponseStatus`) |
| `exception/DuplicateResourceException.java`, `BookNotAvailableException.java` | 409 and 422 business exceptions |
| `exception/GlobalExceptionHandler.java` | `@RestControllerAdvice` + `@ExceptionHandler` methods -> `ErrorResponse` (default profile) |
| `exception/ProblemDetailExceptionHandler.java` | RFC 9457 `ProblemDetail` responses, extends `ResponseEntityExceptionHandler` (`problem` profile) |
| `dto/ErrorResponse.java` | Standard error DTO: timestamp, status, error, message, path, fieldErrors |
| `service/BookService.java` | Throws meaningful exceptions - no try/catch, no status codes |
| `controller/BookController.java` | No try/catch at all; `/crash` simulates a bug |
| `application*.properties` | Default, `problem` (`spring.mvc.problemdetails.enabled`) and `plain` profiles |
| `src/test/...` | MockMvc tests for both error formats |
| `requests.http` | 13 requests that trigger every kind of error |

## Run

```
mvn spring-boot:run                                       # ErrorResponse style
mvn spring-boot:run -Dspring-boot.run.profiles=problem    # ProblemDetail (application/problem+json)
mvn spring-boot:run -Dspring-boot.run.profiles=plain      # Spring Boot's default error JSON
mvn test
```
