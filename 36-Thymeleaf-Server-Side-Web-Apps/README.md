# 36 - Thymeleaf: Server-Side Web Apps

A small **Todo list** web app where the server renders complete HTML pages with
Spring MVC `@Controller` + **Thymeleaf** templates (no JSON API, no JavaScript framework).
Open `lesson.html` for the full lesson.

| File | What it shows |
|------|---------------|
| `controller/TodoController.java` | `@Controller` returning view names, `Model`, `@ModelAttribute`, `BindingResult`, POST-Redirect-GET, flash attributes |
| `web/TodoForm.java` | Form-backing object with validation and `@DateTimeFormat` |
| `service/TodoService.java` | In-memory todos (same service style as a REST app) |
| `model/Todo.java`, `model/Priority.java` | Domain class and enum used in a `<select>` |
| `exception/TodoNotFoundException.java` | 404 -> rendered with `templates/error/404.html` |
| `templates/fragments/layout.html` | Reusable fragments: head (with parameter), navbar, flash message, footer |
| `templates/todos/list.html` | `th:text`, `th:each` (+ status), `th:if/unless`, `th:href`, `th:classappend`, POST forms |
| `templates/todos/form.html` | `th:object`, `th:field`, `th:errors`, `th:errorclass`, select/date/checkbox |
| `templates/error/404.html` | Custom error page |
| `static/css/app.css` | Static resource served at `/css/app.css` |
| `src/test/...` | MockMvc tests: view names, model, validation errors, redirects |

## Run

```
mvn spring-boot:run        # then open http://localhost:8080 in your browser
mvn test
```
