# =====================================================================
# ShopEasy - multi-stage Docker build (topic 66)
#
#   docker build -t shopeasy:1.0.0 .
#   docker run -p 8080:8080 shopeasy:1.0.0            (H2 in-memory)
#   docker compose up --build                          (app + MySQL, see docker-compose.yml)
#
# Stage 1 builds the jar with Maven, stage 2 copies ONLY the jar into a small JRE image.
# Build tools and source code never reach the final image -> smaller and safer.
# =====================================================================

# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy pom.xml first and download dependencies. Docker caches this layer, so when only
# your Java code changes the (slow) dependency download is skipped on the next build.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# Split the fat jar into layers (dependencies change rarely, our classes often).
RUN java -Djarmode=tools -jar target/shopeasy.jar extract --layers --launcher --destination extracted

# ---------- Stage 2: run ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Never run as root inside a container.
RUN groupadd --system shopeasy && useradd --system --gid shopeasy shopeasy

COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

USER shopeasy
EXPOSE 8080

# Container-aware JVM memory: use up to 75% of the container's memory limit for the heap.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
