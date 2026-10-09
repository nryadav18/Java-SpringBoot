# 21 · Unit Testing with JUnit 5

Write automatic tests that prove your code works: JUnit 5 basics, parameterized tests, exception tests,
Mockito mocks for dependencies, and AssertJ's fluent assertions. Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | JUnit Jupiter 5.11.4, Mockito 5.14.2 (+ mockito-junit-jupiter), AssertJ 3.26.3, Surefire 3.5.2 |
| `src/main/java/com/course/unittesting/Calculator.java` | Simple class under test (no dependencies) |
| `src/main/java/com/course/unittesting/bank/BankAccountService.java` | Service with two dependencies passed in the constructor |
| `.../bank/AccountRepository.java`, `NotificationService.java` | Dependency interfaces (mocked in tests) |
| `.../bank/Account.java`, `AccountNotFoundException.java`, `InsufficientFundsException.java` | Domain class and custom exceptions |
| `src/test/java/com/course/unittesting/CalculatorTest.java` | @Test, assertions, lifecycle, @DisplayName, @Nested, @ParameterizedTest, assertThrows, assertAll |
| `src/test/java/com/course/unittesting/bank/BankAccountServiceTest.java` | Mockito: @Mock, @InjectMocks, when/thenReturn, verify, never, ArgumentCaptor |
| `src/test/java/com/course/unittesting/AssertJExamplesTest.java` | AssertJ `assertThat(...)` examples |

## How to run
```
cd "D:\Java-SpringBoot\21-Unit-Testing-With-JUnit5"
mvn test                                   # expect: Tests run: 34, Failures: 0 ... BUILD SUCCESS
mvn test -Dtest=CalculatorTest             # one class
mvn test -Dtest=CalculatorTest#addsTwoNumbers
```
In VS Code use the Testing panel (flask icon) or the green run arrows next to each test. Delete `target/` afterwards (`mvn clean`).
