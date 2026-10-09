# =====================================================================
# Dockerfile - Topic 68
#
# Different from topic 66/67: this is a SINGLE-stage file that only COPIES an
# already-built jar. Why? In CI, the "build" job already compiled and TESTED
# target/app.jar. We reuse exactly that tested jar ("build once, deploy many").
#
# Locally:   mvn -B verify
#            docker build -t shipping-app:local .
#            docker run --rm -p 8080:8080 shipping-app:local
# =====================================================================
FROM eclipse-temurin:17-jre

RUN groupadd --system spring && useradd --system --gid spring spring
WORKDIR /app

COPY target/app.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
