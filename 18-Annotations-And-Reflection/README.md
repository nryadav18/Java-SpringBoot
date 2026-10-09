# 18 · Annotations & Reflection

Annotations are labels on code; reflection lets a program look at and use classes while it runs.
Together they are the "magic" behind Spring (`@Component`, `@Autowired`), JUnit (`@Test`) and Hibernate (`@Entity`).
In this topic you build a tiny Spring-like dependency-injection container yourself.

Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_BuiltInAnnotations.java` | `@Override`, `@Deprecated`, `@FunctionalInterface`, `@SuppressWarnings` |
| `Example02_CustomAnnotation.java` | Writing `@interface`, `@Retention`, `@Target`, elements/defaults, a mini test runner |
| `Example03_ReflectionBasics.java` | `Class`, fields, methods, constructors, `invoke`, private access |
| `Example04_MiniDIContainer.java` | **A mini DI container**: `@MyComponent` scan + `@MyAutowired` field injection |
| `Example05_AnnotationValidator.java` | A mini Bean Validation: `@NotBlank`, `@Min` read with reflection |

## How to run
```
cd "D:\Java-SpringBoot\18-Annotations-And-Reflection"
java Example04_MiniDIContainer.java
```
(Java 17, single-file launch - no compile step needed.)
