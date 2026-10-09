# 28 - Spring Boot Introduction

Your first Spring Boot application: auto-configuration, starters, embedded Tomcat,
a `CommandLineRunner`, a "Hello" REST endpoint, a custom banner and Actuator health.
Open **lesson.html** for the full explanation.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | Spring Boot parent 3.5.6 + starters (web, actuator, devtools, test) |
| `src/main/java/com/course/bootintro/BootIntroApplication.java` | `@SpringBootApplication` + `SpringApplication.run` |
| `src/main/java/com/course/bootintro/HelloController.java` | First REST endpoints `/hello` and `/info` |
| `src/main/java/com/course/bootintro/GreetingService.java` | A normal `@Service` bean injected into the controller |
| `src/main/java/com/course/bootintro/StartupRunner.java` | `CommandLineRunner` that runs once at startup |
| `src/main/resources/application.properties` | Port, app name, actuator exposure, `debug` switch |
| `src/main/resources/banner.txt` | Custom startup banner |
| `src/test/java/com/course/bootintro/BootIntroApplicationTests.java` | `@SpringBootTest` smoke test |
| `requests.http` | Sample requests for the VS Code REST Client |

## Run
```
mvn spring-boot:run          # then open http://localhost:8080/hello
mvn clean package            # build the executable jar
java -jar target/boot-intro-0.0.1-SNAPSHOT.jar
mvn test                     # run the tests
```
