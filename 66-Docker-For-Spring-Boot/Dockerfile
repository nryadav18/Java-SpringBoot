# =====================================================================
# Topic 66 - PRODUCTION-STYLE Dockerfile for a Spring Boot app
#   multi-stage build + layered jar + non-root user + health check
#
# Build:  docker build -t docker-demo:1.0 .
# Run:    docker run -d --name demo -p 8080:8080 docker-demo:1.0
# Test:   curl http://localhost:8080/api/hello
#
# You do NOT need Java or Maven on the machine that builds this image:
# stage 1 brings its own Maven + JDK.
# =====================================================================

# ---------- Stage 1: BUILD the jar with Maven + JDK 17 ----------
# (For real projects pin the exact tag, e.g. maven:3.9.11-eclipse-temurin-17)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy ONLY pom.xml first and download dependencies.
# Docker caches each step (layer). As long as pom.xml does not change, this slow
# "download the internet" step is reused from cache on the next build.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Now copy the source code (changes often) and build the jar (tests run in CI, not here).
COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Stage 2: SPLIT the fat jar into layers ----------
# Spring Boot's "tools" jar mode extracts the jar into 4 folders:
#   dependencies/          third-party jars      (change rarely)
#   spring-boot-loader/    Boot's launcher       (changes almost never)
#   snapshot-dependencies/ SNAPSHOT jars         (change sometimes)
#   application/           YOUR classes + app.jar(changes on every commit)
FROM eclipse-temurin:17-jre-alpine AS extract
WORKDIR /extract
COPY --from=build /workspace/target/docker-demo.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# ---------- Stage 3: the small RUNTIME image (only a JRE, no Maven, no source) ----------
FROM eclipse-temurin:17-jre-alpine

# Security: never run as root inside the container. Create a normal user "spring".
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the layers from "least often changed" to "most often changed".
# When only your code changes, Docker rebuilds/pushes just the LAST small layer.
COPY --from=extract /extract/extracted/dependencies/ ./
COPY --from=extract /extract/extracted/spring-boot-loader/ ./
COPY --from=extract /extract/extracted/snapshot-dependencies/ ./
COPY --from=extract /extract/extracted/application/ ./

USER spring

# Documentation only: the app listens on 8080 inside the container. Publishing to the
# host still needs "-p 8080:8080" on docker run.
EXPOSE 8080

# JVM settings. JAVA_TOOL_OPTIONS is read automatically by every JVM.
# MaxRAMPercentage=75 -> heap may use 75 % of the container's memory limit.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0" \
    SPRING_PROFILES_ACTIVE=docker

# Docker asks this command every 30 s "are you healthy?" (alpine has busybox wget built in).
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Exec form (JSON array): java becomes process 1 and receives "docker stop" signals directly
# -> graceful shutdown works.
ENTRYPOINT ["java", "-jar", "app.jar"]
