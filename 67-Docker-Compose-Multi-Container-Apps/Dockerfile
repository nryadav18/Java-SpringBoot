# =====================================================================
# Dockerfile - Topic 67 (same idea as topic 66: a multi-stage build)
# docker compose builds this automatically because of "build: ." in docker-compose.yml.
# =====================================================================

# ---------- Stage 1: build the jar with Maven + JDK 17 ----------
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy ONLY pom.xml first and download dependencies.
# Docker caches this layer, so later builds skip the download when only code changed.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Stage 2: small runtime image with only a JRE ----------
FROM eclipse-temurin:17-jre

# curl is used by the healthcheck in docker-compose.yml
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Never run as root inside a container
RUN groupadd --system spring && useradd --system --gid spring spring

WORKDIR /app
COPY --from=build /workspace/target/app.jar app.jar
USER spring

EXPOSE 8080

# "exec" makes Java process number 1, so it receives Docker's stop signal (graceful shutdown).
# JAVA_OPTS comes from app.env (see docker-compose.yml).
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
