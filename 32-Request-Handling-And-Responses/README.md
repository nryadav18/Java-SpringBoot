# 32 - Request Handling & Responses

Reading every part of an HTTP request (`@PathVariable`, `@RequestParam`, `@RequestBody`,
`@RequestHeader`) and building precise responses with `ResponseEntity` (status codes, headers,
`Location` for 201 Created). Full CRUD on in-memory **Products**, Jackson annotations,
content negotiation and CORS. Open **lesson.html** for the full explanation.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | web starter (+ commented XML dependency for the content-negotiation experiment) |
| `src/main/java/com/course/requesthandling/RequestHandlingApplication.java` | Main class |
| `src/main/java/com/course/requesthandling/Product.java` | `@JsonProperty`, `@JsonIgnore`, `@JsonFormat` |
| `src/main/java/com/course/requesthandling/ProductRequest.java` | Body for POST / PUT |
| `src/main/java/com/course/requesthandling/ProductPatchRequest.java` | Body for PATCH (all fields optional) |
| `src/main/java/com/course/requesthandling/ProductService.java` | In-memory CRUD, filters and sorting |
| `src/main/java/com/course/requesthandling/ProductController.java` | Full CRUD with `ResponseEntity`, `Location` header, `@RequestHeader`, `@CrossOrigin` |
| `src/main/java/com/course/requesthandling/CorsConfig.java` | Global CORS configuration |
| `src/main/resources/application.properties` | Pretty JSON, fail on unknown JSON fields |
| `src/test/java/.../ProductControllerTest.java` | MockMvc tests for every operation |
| `requests.http` | 12 ready-made requests |

## Run
```
mvn spring-boot:run
curl -i http://localhost:8080/api/products
mvn test
```
