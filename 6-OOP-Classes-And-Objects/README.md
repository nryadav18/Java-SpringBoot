# Topic 6 - OOP: Classes and Objects

Object-Oriented Programming (OOP) lets you model real things (cars, students, bank accounts)
as objects that hold data (fields) and behaviour (methods). This topic covers classes vs objects,
fields, methods, constructors (default, parameterized, overloading, `this`, chaining),
references and memory (stack vs heap), `toString` / `equals` / `hashCode`, and the object
lifecycle with garbage collection.

Open **lesson.html** in your browser for the full lesson.

## Files

| File | What it shows |
|------|---------------|
| `Example01_ClassAndObject.java` | `Car` blueprint, creating objects with `new`, fields, methods, default values |
| `Example02_Constructors.java` | `Student` with default/parameterized/overloaded constructors, `this`, `this(...)` chaining |
| `Example03_BankAccountObjects.java` | `BankAccount` with deposit/withdraw/transfer, objects in an array |
| `Example04_ReferencesAndMemory.java` | References, aliasing, passing objects to methods, `null`, stack vs heap |
| `Example05_ToStringEqualsHashCode.java` | `Book` with `toString`, `equals`, `hashCode`, and why HashSet needs them |
| `Example06_ObjectLifecycleAndGC.java` | When objects become unreachable, garbage collection, memory before/after |

## How to run

```
cd "D:\Java-SpringBoot\6-OOP-Classes-And-Objects"
java Example01_ClassAndObject.java
```

Expected output is written at the bottom of each file.
