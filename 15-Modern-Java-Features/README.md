# 15 · Modern Java Features (Java 9 – 17)

The features that make Java 17 code short and safe: `var`, text blocks, records,
enums with behaviour, sealed types, switch expressions, pattern matching for
`instanceof`, helpful NullPointerExceptions, `List.of`/`Map.of` and new String methods.
Records will be your DTOs in every Spring REST API later in the course.

Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_VarAndTextBlocks.java` | `var` rules; text blocks for JSON/SQL/HTML, `\` and `\s` escapes, `formatted()` |
| `Example02_Records.java` | Records: accessors, equals/hashCode/toString, compact constructor validation, withers, DTOs |
| `Example03_Enums.java` | Enums with fields/constructor/methods, switch on enum, constant-specific methods, EnumMap/EnumSet |
| `Example04_SealedClasses.java` | `sealed` / `permits` / `final` / `non-sealed` with a Payment hierarchy |
| `Example05_SwitchAndPatternMatching.java` | Switch expressions (`->`, multiple labels, `yield`, exhaustive) and `instanceof` patterns |
| `Example06_NpeCollectionsAndStrings.java` | Helpful NPE messages, immutable collection factories, new String methods |

## How to run
```
cd "D:\Java-SpringBoot\15-Modern-Java-Features"
java Example02_Records.java
```
Every file ends with an `/* Output: ... */` comment showing what you should see.
