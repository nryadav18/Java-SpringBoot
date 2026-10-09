# Topic 7 - OOP: Encapsulation and Inheritance

Two pillars of OOP. **Encapsulation** hides an object's data (`private` fields) and allows
changes only through methods that check the rules. **Inheritance** (`extends`) lets a class
re-use and specialise another class (a Manager IS-A Employee). This topic also covers `super`,
constructor call order, method overriding with `@Override`, the `Object` class, `protected`,
types of inheritance, and IS-A vs HAS-A (composition over inheritance).

Open **lesson.html** in your browser for the full lesson.

## Files

| File | What it shows |
|------|---------------|
| `Example01_Encapsulation.java` | `BankAccount` with private fields, getters, validated setter, read-only field, no `setBalance` |
| `Example02_InheritanceBasics.java` | `Employee` -> `Manager` / `Developer`: inherited and added methods, `super(...)` |
| `Example03_SuperAndConstructorOrder.java` | `Vehicle` -> `Car` -> `ElectricCar`: constructor order, `super(...)`, implicit `super()`, `super.method()` |
| `Example04_MethodOverriding.java` | `Account` -> `SavingsAccount` / `CurrentAccount`: overriding, `@Override`, overriding vs overloading |
| `Example05_ObjectClassAndProtected.java` | `Object` methods, `protected` vs `private`, single/multilevel/hierarchical inheritance |
| `Example06_CompositionVsInheritance.java` | `SportsCar` HAS-A `Engine` (swap at runtime) vs `Truck` IS-A `MotorVehicle` |

## How to run

```
cd "D:\Java-SpringBoot\7-OOP-Encapsulation-And-Inheritance"
java Example01_Encapsulation.java
```

Expected output is written at the bottom of each file.
