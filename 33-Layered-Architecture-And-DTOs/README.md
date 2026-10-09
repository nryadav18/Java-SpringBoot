# 33 - Layered Architecture & DTOs

A clean "Employee Management" REST API split into **controller -> service -> repository** layers,
using **DTOs (Java records)** and a **manual mapper**. The repository is an interface with an
in-memory implementation, so it can later be swapped for JDBC/JPA without touching the service.
Lombok is explained in the lesson but deliberately **not** used in the code.

Open `lesson.html` in your browser for the full lesson.

| File | What it shows |
|------|---------------|
| `controller/EmployeeController.java` | Thin web layer: URLs, status codes, DTOs in/out |
| `service/EmployeeService.java` | Business rules (unique email, must exist), uses the repository interface |
| `repository/EmployeeRepository.java` | The storage contract (interface) |
| `repository/InMemoryEmployeeRepository.java` | Thread-safe Map-based implementation |
| `model/Employee.java` | Internal domain model (has salary and HR notes - never exposed) |
| `dto/EmployeeRequest.java`, `EmployeeResponse.java`, `DepartmentSummary.java` | Records used as DTOs |
| `mapper/EmployeeMapper.java` | Converts DTO <-> domain model in one place |
| `exception/*` | Meaningful exceptions mapped to 404 / 409 with `@ResponseStatus` |
| `config/SampleDataLoader.java` | Loads 3 sample employees at startup |
| `src/test/...` | Plain unit tests for the service and the mapper |
| `requests.http` | Sample requests for the VS Code REST Client extension |

## Run

```
mvn spring-boot:run          # then open http://localhost:8080/api/employees
mvn test                     # runs the unit tests
```
