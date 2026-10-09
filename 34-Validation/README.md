# 34 - Validation

Validating input at the API boundary with **Jakarta Bean Validation** (`spring-boot-starter-validation`,
engine = Hibernate Validator). Open `lesson.html` for the full lesson.

| File | What it shows |
|------|---------------|
| `dto/RegisterUserRequest.java` | Built-in constraints (`@NotBlank`, `@Size`, `@Email`, `@Min`, `@Max`, `@Past`), custom ones, nested `@Valid`, list-element constraints |
| `dto/AddressRequest.java` | Nested DTO with `@Pattern` |
| `dto/BookingRequest.java` | `@Future`, `@Positive`, `@PositiveOrZero`, `@Max`, `@DecimalMin` |
| `dto/ProductRequest.java` + `validation/ValidationGroups.java` | Validation groups (`OnCreate` / `OnUpdate`) |
| `validation/StrongPassword*.java` | Custom constraint `@StrongPassword` + its validator |
| `validation/ValidPhoneNumber*.java` | Custom constraint `@ValidPhoneNumber` + its validator |
| `controller/UserController.java` | `@Valid @RequestBody`, constraints on `@PathVariable` / `@RequestParam` |
| `controller/BookingController.java`, `ProductController.java` | `@Valid` vs `@Validated(Group.class)` |
| `application.properties` | `server.error.include-message` / `include-binding-errors` to see default error details |
| `src/test/...` | Plain validator tests + MockMvc tests (400 on invalid input) |
| `requests.http` | Valid and invalid sample requests |

## Run

```
mvn spring-boot:run      # http://localhost:8080 - then use requests.http
mvn test
```

Nicely formatted error responses come in topic 35 (Global Exception Handling).
