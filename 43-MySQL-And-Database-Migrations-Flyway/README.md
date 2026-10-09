# 43 - MySQL and Database Migrations with Flyway

Moving from H2 to MySQL, and letting **Flyway** (not Hibernate) create and change the tables.
Open **lesson.html** for the full lesson.

## Files
- `pom.xml` - web, data-jpa, validation, flyway-core, flyway-mysql, h2, mysql-connector-j.
- `docker-compose.yml` - starts MySQL 8.4 (`docker compose up -d`).
- `src/main/resources/application.properties` - default profile = H2 in-memory, `ddl-auto=validate`.
- `src/main/resources/application-mysql.properties` - the `mysql` profile (URL, user, password).
- `src/main/resources/db/migration/V1__..., V2__..., V3__...` - versioned Flyway migrations.
- `src/main/java/.../book/*` - Book entity, repository, service, DTOs, REST controller.
- `src/main/java/.../config/DatabaseInfoController.java` - `GET /api/db-info` shows the real database + applied migrations.
- `requests.http` - sample requests for the VS Code REST Client.

## Run
```
# H2 (nothing to install)
mvn spring-boot:run

# MySQL
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
Then open http://localhost:8080/api/db-info and http://localhost:8080/api/books
