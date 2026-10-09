# =====================================================================
# Dockerfile - Topic 69 (multi-stage, same idea as topic 66)
#   docker build -t menu-service:1.0.0 .
# =====================================================================
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:17-jre
# Numeric user id, so Kubernetes "runAsNonRoot: true" can verify it is not root
RUN groupadd --system --gid 1001 spring && useradd --system --uid 1001 --gid spring spring
WORKDIR /app
COPY --from=build /workspace/target/app.jar app.jar
USER 1001
EXPOSE 8080
# Exec form: java is PID 1 and receives SIGTERM directly -> graceful shutdown works.
# JVM flags come from the JAVA_TOOL_OPTIONS env var (k8s/configmap.yaml).
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
