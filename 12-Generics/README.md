# Topic 12 - Generics

Write classes and methods that work with ANY type while staying type-safe: generic classes (`Box<T>`),
generic methods, bounded types (`<T extends Number>`), wildcards (`?`, `? extends`, `? super`, PECS),
type erasure, and a generic `Repository<T, ID>` - a preview of Spring Data. Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_WhyGenerics.java` | Raw list + casts + ClassCastException vs `List<String>` |
| `Example02_GenericClasses.java` | `Box<T>`, `Pair<K, V>`, `Shelf<E>`, `ApiResponse<T>` |
| `Example03_GenericMethods.java` | `<T>` methods, type inference, returning T, two type parameters |
| `Example04_BoundedTypes.java` | `<T extends Number>`, `<T extends Comparable<T>>`, multiple bounds, bounded class |
| `Example05_Wildcards.java` | `List<?>`, `? extends` (read), `? super` (write), PECS copy |
| `Example06_TypeErasure.java` | What erasure means and its limits (no `new T()`, no `instanceof List<String>`) |
| `Example07_GenericRepository.java` | Generic interface `Repository<T, ID>` with one in-memory implementation |

## How to run (terminal opened in this folder)
```
java Example01_WhyGenerics.java
java Example07_GenericRepository.java
```
