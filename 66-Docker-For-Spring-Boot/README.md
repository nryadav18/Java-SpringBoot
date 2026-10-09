# Topic 66 - Docker for Spring Boot

A tiny Spring Boot app (`docker-demo`) packaged into a Docker image. Open `lesson.html` for the full lesson.

## Folder contents
| Path | What it shows |
|------|---------------|
| `src/...` | `HelloController` (`/api/hello`, `/api/info`), `AppProperties` (config overridable by env vars), `application.yml`. |
| `Dockerfile` | Production style: multi-stage build, layered jar, non-root user, HEALTHCHECK. |
| `Dockerfile.simple` | The simplest 5-line Dockerfile (jar built on your machine first). |
| `.dockerignore` | What NOT to send to the Docker build. |
| `requests.http` | Sample requests. |

## Run
```
# without Docker
mvn spring-boot:run

# with Docker (Docker Desktop must be running)
docker build -t docker-demo:1.0 .
docker run -d --name demo -p 8080:8080 -e APP_GREETING="Hello from Docker" docker-demo:1.0
curl http://localhost:8080/api/hello
docker logs -f demo          # Ctrl + C to stop following
docker stop demo && docker rm demo

# Buildpacks (no Dockerfile at all)
mvn spring-boot:build-image
```
