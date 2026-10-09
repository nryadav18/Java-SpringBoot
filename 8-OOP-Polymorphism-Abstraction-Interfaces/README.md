# Topic 8 - OOP: Polymorphism, Abstraction and Interfaces

The last two OOP pillars. **Polymorphism** = one name, many forms (overloading at compile
time, overriding at runtime). **Abstraction** = show what an object does, hide how
(abstract classes and interfaces). Ends with the most important idea for Spring:
**program to an interface** for loose coupling.

Open **lesson.html** in your browser for the full lesson.

## Files

| File | What it shows |
|------|---------------|
| `Example01_CompileTimePolymorphism.java` | Method overloading (number/type/order of parameters), type promotion |
| `Example02_RuntimePolymorphism.java` | `Notification` -> Email/SMS/Push: parent variable, child object, dynamic dispatch |
| `Example03_CastingAndInstanceof.java` | Upcasting, downcasting, `ClassCastException`, `instanceof`, pattern matching (Java 16+) |
| `Example04_AbstractClasses.java` | `abstract class Shape` with Circle/Rectangle/Triangle, abstract + concrete methods |
| `Example05_Interfaces.java` | `Payment` interface (CreditCard/UPI/PayPal): constants, default, static, private methods |
| `Example06_MultipleInterfacesAndFunctional.java` | One class, many interfaces; default-method conflict; `@FunctionalInterface` + lambda preview |
| `Example07_ProgrammingToInterface.java` | Tight vs loose coupling: `CheckoutService` + `PaymentGateway`, fake gateway for testing |

## How to run

```
cd "D:\Java-SpringBoot\8-OOP-Polymorphism-Abstraction-Interfaces"
java Example01_CompileTimePolymorphism.java
```

Expected output is written at the bottom of each file.
