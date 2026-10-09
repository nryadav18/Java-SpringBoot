# 67 - Docker Compose: Multi-Container Apps

Run a Spring Boot "Library" API **and** a MySQL database together with one command.
Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `docker-compose.yml` | The whole app: `app` + `mysql` (+ optional `redis` profile), network, volume, healthchecks, `depends_on` |
| `Dockerfile` | Multi-stage build of the Spring Boot jar (Maven -> JRE image, non-root user) |
| `.dockerignore` | Files not sent to the Docker build |
| `.env.example` | Template for `.env` (passwords). Copy to `.env`; never commit `.env` |
| `app.env` | Non-secret env vars for the app container (`env_file`) |
| `db/init.sql` | Creates the `books` table + sample rows on MySQL's first start |
| `src/main/...` | Small REST API: `Book`, `BookRepository`, `BookController` |
| `application.properties` / `application-docker.properties` | H2 on laptop vs MySQL inside Compose |
| `requests.http` | Sample requests for the REST Client extension |

## Run
```powershell
mvn spring-boot:run                 # without Docker (H2)
Copy-Item .env.example .env         # once
docker compose up -d --build        # app + MySQL
docker compose ps                   # both should be "healthy"
curl http://localhost:8080/api/books
docker compose logs -f app
docker compose down                 # stop (keeps data)   |  docker compose down -v  (wipe data)
```
