# 44 - REST API Best Practices and OpenAPI

A well-designed, self-documenting **Task API**: clean URLs, correct status codes, pagination/filtering/sorting,
one error format (ProblemDetail), versioning, idempotency, and Swagger UI via springdoc.
Open **lesson.html** for the full lesson.

## Files (under `src/main/java/com/course/taskapi`)
- `TaskApiApplication.java` - main class.
- `config/OpenApiConfig.java` - API title/version/contact + Swagger groups (v1, v2, versioning-demo).
- `config/DataSeeder.java` - inserts 12 sample tasks.
- `task/Task.java`, `TaskStatus`, `TaskPriority` - entity and enums.
- `task/TaskRepository.java` - one search query with optional filters + `Pageable`.
- `task/TaskService.java` - business logic, PUT vs PATCH, Idempotency-Key handling.
- `task/TaskController.java` - `/api/v1/tasks` with `@Tag`, `@Operation`, `@ApiResponse`.
- `task/TaskV2Controller.java` - `/api/v2/tasks` (URI versioning).
- `task/HeaderVersionedTaskController.java` - `/api/tasks/{id}` + `X-API-Version` header.
- `task/*Request.java`, `TaskResponse`, `TaskSummaryV2` - DTO records with `@Schema`.
- `common/PageResponse.java` - consistent page shape; `common/GlobalExceptionHandler.java` - ProblemDetail errors.
- `requests.http` - every request, ready to click.

## Run
```
mvn spring-boot:run
```
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs/v1
- API: http://localhost:8080/api/v1/tasks
