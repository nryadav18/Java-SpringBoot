# 23 · Spring IoC Container & Beans

How the Spring container really works: `BeanFactory` vs `ApplicationContext`, the stereotype annotations
(`@Component`, `@Service`, `@Repository`, `@Controller`), `@ComponentScan`, `@Configuration` + `@Bean`,
bean names, `getBean` and the container start-up sequence. Domain: a small Library.
Open `lesson.html` in a browser for the full lesson.

## Files (`src/main/java/com/course/ioccontainer/`)
| File | What it shows |
|------|---------------|
| `App.java` | All demos: lazy BeanFactory vs eager ApplicationContext, bean names, `getBean` variants, using wired beans |
| `config/AppConfig.java` | `@Configuration` + `@ComponentScan` + `@Bean` methods (JDK classes `Clock`, `NumberFormat`), custom names/aliases |
| `controller/BookController.java` | `@Controller` stereotype, constructor injection |
| `service/BookService.java` | `@Service` stereotype, 3 injected dependencies (scanned + `@Bean`) |
| `service/FineCalculator.java` | `@Component("lateFeeCalculator")` - custom bean name |
| `repository/BookRepository.java` | `@Repository` stereotype, in-memory "database" |
| `model/Book.java` | A record - plain data, NOT a bean |
| `legacy/ReportGenerator.java` | Plain class registered by hand for the BeanFactory demo |

## Run
```
cd "D:\Java-SpringBoot\23-Spring-IoC-Container-And-Beans"
mvn -q compile exec:java
```
