# 68 - CI/CD with GitHub Actions

A small "shipping cost" Spring Boot API **with tests**, plus a GitHub Actions pipeline that
builds, tests, and pushes a Docker image on every push. Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `.github/workflows/ci.yml` | The pipeline: build + test (`mvn -B verify`), Docker build & push, deploy placeholder |
| `.github/dependabot.yml` | Automatic dependency-update pull requests |
| `src/main/java/.../shipping/ShippingService.java` | The business rule protected by tests |
| `src/main/java/.../shipping/ShippingController.java` | `GET /api/shipping/quote?weightKg=2.5&express=true` |
| `src/test/java/.../ShippingServiceTest.java` | Unit tests (JUnit 5 + AssertJ, parameterized) |
| `src/test/java/.../ShippingControllerTest.java` | `@WebMvcTest` + MockMvc tests |
| `pom.xml` | Spring Boot 3.5.6 + JaCoCo coverage report on `mvn verify` |
| `Dockerfile` / `.dockerignore` | Image that copies the already-tested `target/app.jar` |
| `requests.http` | Sample requests |

## Run locally
```powershell
mvn -B verify          # exactly what CI runs (13 tests); coverage: target/site/jacoco/index.html
mvn spring-boot:run    # http://localhost:8080/api/shipping/quote?weightKg=2.5
```

## Run on GitHub
Make **this folder** the root of a new GitHub repository (GitHub only reads `.github/` at the repo root),
add secrets `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN`, push to `main`, then open the **Actions** tab.
