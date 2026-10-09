# 29 - Configuration Properties & Profiles

How a Spring Boot app reads its settings: `application.yml` vs `.properties`, `@Value`,
type-safe `@ConfigurationProperties` (a validated record), profiles (`dev` / `prod`),
the external configuration order and secrets from environment variables.
Open **lesson.html** for the full explanation.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | web + validation + configuration-processor (auto-complete for our keys) |
| `src/main/java/com/course/configprops/ConfigPropsApplication.java` | `@ConfigurationPropertiesScan` + startup printout of the config |
| `src/main/java/com/course/configprops/ShopProperties.java` | Validated `@ConfigurationProperties` record with a nested record |
| `src/main/java/com/course/configprops/ConfigController.java` | `@Value`, defaults, `Environment`, masked secret |
| `src/main/resources/application.yml` | Base config for all profiles |
| `src/main/resources/application-dev.yml` / `application-prod.yml` | Profile overrides |
| `examples/application.properties.example` | Same config in `.properties` format (reference only) |
| `src/test/java/.../ConfigPropsApplicationTests.java` | Checks the binding + dev override |
| `requests.http` | Sample requests |

## Run
```
mvn spring-boot:run                                    # dev profile, port 8080
mvn spring-boot:run -Dspring-boot.run.profiles=prod    # prod profile, port 8081
$env:PAYMENT_API_KEY="sk_test_12345678"; mvn spring-boot:run   # PowerShell: pass a secret
mvn test
```
