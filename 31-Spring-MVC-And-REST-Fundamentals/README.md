# 31 - Spring MVC & REST Fundamentals

HTTP basics, what REST means, how Spring MVC's `DispatcherServlet` routes a request to a
controller, and how Jackson turns Java objects into JSON. Code: an in-memory **Book API**.
Open **lesson.html** for the full explanation.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | Just `spring-boot-starter-web` (+ test) |
| `src/main/java/com/course/restbasics/RestBasicsApplication.java` | Main class |
| `src/main/java/com/course/restbasics/Book.java` | The resource, a record -> JSON |
| `src/main/java/com/course/restbasics/BookService.java` | Thread-safe in-memory list of books |
| `src/main/java/com/course/restbasics/BookController.java` | `@RestController` with GET / POST / DELETE endpoints |
| `src/main/java/com/course/restbasics/HomeController.java` | `@Controller` + `@ResponseBody` (contrast with `@RestController`) |
| `src/main/resources/application.properties` | Port, pretty JSON, error message setting |
| `src/test/java/.../BookControllerTest.java` | First MockMvc tests |
| `requests.http` | Ready-made requests for the VS Code REST Client |

## Run
```
mvn spring-boot:run
curl http://localhost:8080/api/books
curl -X POST http://localhost:8080/api/books -H "Content-Type: application/json" -d "{\"title\":\"Java Concurrency in Practice\",\"author\":\"Brian Goetz\",\"year\":2006,\"price\":800}"
mvn test
```
