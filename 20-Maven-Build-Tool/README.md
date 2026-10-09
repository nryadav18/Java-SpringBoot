# 20 · Maven Build Tool

Maven downloads libraries, compiles, tests and packages your Java project from one recipe file: `pom.xml`.
Every Spring Boot project in this course is a Maven project. Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | Fully commented POM: GAV, properties, dependencies + scopes, plugins (compiler, surefire, exec, shade), resource filtering |
| `src/main/java/com/course/mavendemo/App.java` | Main class; reads `app.properties` from the classpath |
| `src/main/java/com/course/mavendemo/LabelFormatter.java` | Uses the Apache Commons Lang 3.17.0 library (`StringUtils`) |
| `src/main/resources/app.properties` | Resource file; `${project.version}` is filled in by Maven |
| `src/test/java/com/course/mavendemo/LabelFormatterTest.java` | A JUnit 5 test run by `mvn test` (details in Topic 21) |
| `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties` | Maven Wrapper: build without installing Maven (pins Maven 3.9.11) |

## How to run
```
cd "D:\Java-SpringBoot\20-Maven-Build-Tool"
mvn -q compile exec:java                 # run through Maven
mvn clean package                        # test + build target/maven-demo-1.0.0.jar
java -jar target/maven-demo-1.0.0.jar    # run the fat JAR
.\mvnw.cmd clean package                 # same, using the wrapper (Windows)
mvn dependency:tree                      # see direct + transitive dependencies
```
Delete `target/` when done (`mvn clean`).
